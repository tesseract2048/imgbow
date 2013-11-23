package org.tjucs.imgbow.util;

import java.util.ArrayList;
import java.util.Arrays;

/*
** KDTree.java by Julian Kent
** Licenced under the  Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
** See full licencing details here: http://creativecommons.org/licenses/by-nc-sa/3.0/
**
** For additional licencing rights please contact jkflying@gmail.com
**
*/

public abstract class KDTree<T> {

    // use a big bucketSize so that we have less node bounds (for more cache
    // hits) and better splits
    private static final int _bucketSize = 50;

    private final int _dimensions;

    private int _nodes;

    private final Node root;

    private final ArrayList<Node> nodeList = new ArrayList<Node>();

    // prevent GC from having to collect _bucketSize*dimensions*8 bytes each
    // time a leaf splits
    private double[] mem_recycle;

    // the starting values for bounding boxes, for easy access
    private final double[] bounds_template;

    // one big self-expanding array to keep all the node bounding boxes so that
    // they stay in cache
    // node bounds available at:
    // low: 2 * _dimensions * node.index + 2 * dim
    // high: 2 * _dimensions * node.index + 2 * dim + 1
    private final ContiguousDoubleArrayList nodeMinMaxBounds;

    public KDTree(int dimensions) {
        _dimensions = dimensions;

        // initialise this big so that it ends up in 'old' memory
        nodeMinMaxBounds = new ContiguousDoubleArrayList(512 * 1024 / 8 + 2
                * _dimensions);
        mem_recycle = new double[_bucketSize * dimensions];

        bounds_template = new double[2 * _dimensions];
        Arrays.fill(bounds_template, Double.NEGATIVE_INFINITY);
        for (int i = 0, max = 2 * _dimensions; i < max; i += 2)
            bounds_template[i] = Double.POSITIVE_INFINITY;

        // and.... start!
        root = new Node();
    }

    public int nodes() {
        return _nodes;
    }

    public int size() {
        return root.entries;
    }

    public int addPoint(double[] location, T payload) {

        Node addNode = root;
        // Do a Depth First Search to find the Node where 'location' should be
        // stored
        while (addNode.pointLocations == null) {
            addNode.expandBounds(location);
            if (location[addNode.splitDim] < addNode.splitVal)
                addNode = nodeList.get(addNode.lessIndex);
            else
                addNode = nodeList.get(addNode.moreIndex);
        }
        addNode.expandBounds(location);

        int nodeSize = addNode.add(location, payload);

        if (nodeSize % _bucketSize == 0)
            // try splitting again once every time the node passes a _bucketSize
            // multiple
            addNode.split();

        return root.entries;
    }

    public ArrayList<SearchResult<T>> nearestNeighbours(
            double[] searchLocation, int K) {
        IntStack stack = new IntStack();
        PrioQueue<T> results = new PrioQueue<T>(K, true);

        stack.push(root.index);

        int added = 0;

        while (stack.size() > 0) {
            int nodeIndex = stack.pop();
            if (added < K
                    || results.peekPrio() > pointRectDist(nodeIndex,
                            searchLocation))
                added += nodeList.get(nodeIndex).search(searchLocation, stack,
                        results);
        }

        ArrayList<SearchResult<T>> returnResults = new ArrayList<SearchResult<T>>(
                K);
        double[] priorities = results.priorities;
        Object[] elements = results.elements;
        for (int i = 0; i < K; i++) {// forward (closest first)
            @SuppressWarnings("unchecked")
            SearchResult<T> s = new SearchResult<T>(priorities[i],
                    (T) elements[i]);
            returnResults.add(s);
        }
        return returnResults;
    }

    abstract double pointRectDist(int offset, final double[] location);

    abstract double pointDist(double[] arr, double[] location, int index);

    public static class Euclidean<T> extends KDTree<T> {
        public Euclidean(int dims) {
            super(dims);
        }

        double pointRectDist(int offset, final double[] location) {
            offset *= (2 * super._dimensions);
            double distance = 0;
            final double[] array = super.nodeMinMaxBounds.array;
            for (int i = 0; i < location.length; i++, offset += 2) {

                double diff = 0;
                double bv = array[offset];
                double lv = location[i];
                if (bv > lv)
                    diff = bv - lv;
                else {
                    bv = array[offset + 1];
                    if (lv > bv)
                        diff = lv - bv;
                }
                distance += sqr(diff);
            }
            return distance;
        }

        double pointDist(double[] arr, double[] location, int index) {
            // final double[] arr = searchNode.pointLocations.array;
            double distance = 0;
            int offset = (index + 1) * super._dimensions;

            for (int i = super._dimensions; i-- > 0;) {
                distance += sqr(arr[--offset] - location[i]);
            }
            return distance;
        }

    }

    public static class Manhattan<T> extends KDTree<T> {
        public Manhattan(int dims) {
            super(dims);
        }

        double pointRectDist(int offset, final double[] location) {
            offset *= (2 * super._dimensions);
            double distance = 0;
            final double[] array = super.nodeMinMaxBounds.array;
            for (int i = 0; i < location.length; i++, offset += 2) {

                double diff = 0;
                double bv = array[offset];
                double lv = location[i];
                if (bv > lv)
                    diff = bv - lv;
                else {
                    bv = array[offset + 1];
                    if (lv > bv)
                        diff = lv - bv;
                }
                distance += (diff);
            }
            return distance;
        }

        double pointDist(double[] arr, double[] location, int index) {
            // final double[] arr = searchNode.pointLocations.array;
            double distance = 0;
            int offset = (index + 1) * super._dimensions;

            for (int i = super._dimensions; i-- > 0;) {
                distance += Math.abs(arr[--offset] - location[i]);
            }
            return distance;
        }

    }

    // NB! This Priority Queue keeps things with the LOWEST priority.
    // If you want highest priority items kept, negate your values
    private static class PrioQueue<S> {

        Object[] elements;

        double[] priorities;

        private double minPrio;

        private int size;

        PrioQueue(int size, boolean prefill) {
            elements = new Object[size];
            priorities = new double[size];
            Arrays.fill(priorities, Double.POSITIVE_INFINITY);
            if (prefill) {
                minPrio = Double.POSITIVE_INFINITY;
                this.size = size;
            }
        }

        // uses O(log(n)) comparisons and one big shift of size O(N)
        // and is MUCH simpler than a heap --> faster on small sets, faster JIT

        void addNoGrow(S value, double priority) {
            int index = searchFor(priority);
            int nextIndex = index + 1;
            int length = size - index - 1;// remove dependancy on nextIndex
            System.arraycopy(elements, index, elements, nextIndex, length);
            System.arraycopy(priorities, index, priorities, nextIndex, length);
            elements[index] = value;
            priorities[index] = priority;

            minPrio = priorities[size - 1];
        }

        int searchFor(double priority) {
            int i = size - 1;
            int j = 0;
            while (i >= j) {
                int index = (i + j) >>> 1;

                if (priorities[index] < priority)
                    j = index + 1;
                else
                    i = index - 1;
            }
            return j;
        }

        double peekPrio() {
            return minPrio;
        }
    }

    public static class SearchResult<S> {
        public double distance;

        public S payload;

        SearchResult(double dist, S load) {
            distance = dist;
            payload = load;
        }
    }

    private class Node {

        // for accessing bounding box data
        // - if trees weren't so unbalanced might be better to use an implicit
        // heap?
        int index;

        // keep track of size of subtree
        int entries;

        // leaf
        ContiguousDoubleArrayList pointLocations;

        ArrayList<T> pointPayloads = new ArrayList<T>(_bucketSize);

        // stem
        // Node less, more;
        int lessIndex, moreIndex;

        int splitDim;

        double splitVal;

        Node() {
            this(new double[_bucketSize * _dimensions]);
        }

        Node(double[] pointMemory) {
            pointLocations = new ContiguousDoubleArrayList(pointMemory);
            index = _nodes++;
            nodeList.add(this);
            nodeMinMaxBounds.add(bounds_template);
        }

        // returns number of points added to results
        int search(double[] searchLocation, IntStack stack, PrioQueue<T> results) {
            if (pointLocations == null) {

                if (searchLocation[splitDim] < splitVal)
                    stack.push(moreIndex).push(lessIndex);// less will be popped
                                                          // first
                else
                    stack.push(lessIndex).push(moreIndex);// more will be popped
                                                          // first

            } else {
                int updated = 0;
                for (int j = entries; j-- > 0;) {
                    double distance = pointDist(pointLocations.array,
                            searchLocation, j);
                    if (results.peekPrio() > distance) {
                        updated++;
                        results.addNoGrow(pointPayloads.get(j), distance);
                    }
                }
                return updated;
            }
            return 0;
        }

        void expandBounds(double[] location) {
            entries++;
            int mio = index * 2 * _dimensions;
            for (int i = 0; i < _dimensions; i++) {
                nodeMinMaxBounds.array[mio] = Math.min(
                        nodeMinMaxBounds.array[mio++], location[i]);
                nodeMinMaxBounds.array[mio] = Math.max(
                        nodeMinMaxBounds.array[mio++], location[i]);
            }
        }

        int add(double[] location, T load) {
            pointLocations.add(location);
            pointPayloads.add(load);
            return entries;
        }

        void split() {
            int offset = index * 2 * _dimensions;

            double diff = 0;
            for (int i = 0; i < _dimensions; i++) {
                double min = nodeMinMaxBounds.array[offset];
                double max = nodeMinMaxBounds.array[offset + 1];
                if (max - min > diff) {
                    double mean = 0;
                    for (int j = 0; j < entries; j++)
                        mean += pointLocations.array[i + _dimensions * j];

                    mean = mean / entries;
                    double varianceSum = 0;

                    for (int j = 0; j < entries; j++)
                        varianceSum += sqr(mean
                                - pointLocations.array[i + _dimensions * j]);

                    if (varianceSum > diff * entries) {
                        diff = varianceSum / entries;
                        splitVal = mean;

                        splitDim = i;
                    }
                }
                offset += 2;
            }

            // kill all the nasties
            if (splitVal == Double.POSITIVE_INFINITY)
                splitVal = Double.MAX_VALUE;
            else if (splitVal == Double.NEGATIVE_INFINITY)
                splitVal = Double.MIN_VALUE;
            else if (splitVal == nodeMinMaxBounds.array[index * 2 * _dimensions
                    + 2 * splitDim + 1])
                splitVal = nodeMinMaxBounds.array[index * 2 * _dimensions + 2
                        * splitDim];

            Node less = new Node(mem_recycle);// recycle that memory!
            Node more = new Node();
            lessIndex = less.index;
            moreIndex = more.index;

            // reduce garbage by factor of _bucketSize by recycling this array
            double[] pointLocation = new double[_dimensions];
            for (int i = 0; i < entries; i++) {
                System.arraycopy(pointLocations.array, i * _dimensions,
                        pointLocation, 0, _dimensions);
                T load = pointPayloads.get(i);

                if (pointLocation[splitDim] < splitVal) {
                    less.expandBounds(pointLocation);
                    less.add(pointLocation, load);
                } else {
                    more.expandBounds(pointLocation);
                    more.add(pointLocation, load);
                }
            }
            if (less.entries * more.entries == 0) {
                // one of them was 0, so the split was worthless. throw it away.
                _nodes -= 2;// recall that bounds memory
                nodeList.remove(moreIndex);
                nodeList.remove(lessIndex);
            } else {

                // we won't be needing that now, so keep it for the next split
                // to reduce garbage
                mem_recycle = pointLocations.array;

                pointLocations = null;

                pointPayloads.clear();
                pointPayloads = null;
            }
        }

    }

    private static class ContiguousDoubleArrayList {
        double[] array;

        int size;

        ContiguousDoubleArrayList(int size) {
            this(new double[size]);
        }

        ContiguousDoubleArrayList(double[] data) {
            array = data;
        }

        ContiguousDoubleArrayList add(double[] da) {
            if (size + da.length > array.length)
                array = Arrays.copyOf(array, (array.length + da.length) * 2);

            System.arraycopy(da, 0, array, size, da.length);
            size += da.length;
            return this;
        }
    }

    private static class IntStack {
        int[] array;

        int size;

        IntStack() {
            this(64);
        }

        IntStack(int size) {
            this(new int[size]);
        }

        IntStack(int[] data) {
            array = data;
        }

        IntStack push(int i) {
            if (size >= array.length)
                array = Arrays.copyOf(array, (array.length + 1) * 2);

            array[size++] = i;
            return this;
        }

        int pop() {
            return array[--size];
        }

        int size() {
            return size;
        }
    }

    static final double sqr(double d) {
        return d * d;
    }

}
