package steve6472.planetoid;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class QuadTree<T>
{
    private static final int MAX_PER_LEAF_DEFAULT = 4;

    public int maxPerLeaf;
    private Set<Entry<T>> entries = new HashSet<>();

    private final double x, y, width, height;

    private boolean divided = false;
    private QuadTree<T> nodeNE, nodeSE, nodeSW, nodeNW;

    public QuadTree(double x, double y, double width, double height)
    {
        this(MAX_PER_LEAF_DEFAULT, x, y, width, height);
    }

    public QuadTree(int maxPerLeaf, double x, double y, double width, double height)
    {
        this.maxPerLeaf = maxPerLeaf;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean insert(T object, double x, double y)
    {
        if (!containsPoint(x, y))
        {
            return false;
        }

        if (!divided && entries.size() < maxPerLeaf)
        {
            entries.add(new Entry<>(object, x, y));
            return true;
        } else
        {
            if (!divided)
            {
                subdivide();

                entries.forEach(e -> insert(e.obj, e.x, e.y));
                entries.clear();
                entries = null;
            }

            if (this.nodeNE.insert(object, x, y)) return true;
            if (this.nodeSE.insert(object, x, y)) return true;
            if (this.nodeSW.insert(object, x, y)) return true;
            if (this.nodeNW.insert(object, x, y)) return true;

            // ???
            return false;
        }
    }

    private static boolean containsPoint(double px, double py, double x, double y, double width, double height)
    {
        boolean withinXBounds = (px >= x - width) && (px < x + width);
        boolean withinYBounds = (py >= y - height) && (py < y + height);

        return withinXBounds && withinYBounds;
    }

    public boolean containsPoint(double x, double y)
    {
       return containsPoint(x, y, this.x, this.y, this.width, this.height);
    }

    public boolean intersects(double x, double y, double width, double height) 
    {
        double thisLeft = this.x - this.width;
        double thisRight = this.x + this.width;
        double thisTop = this.y + this.height;
        double thisBottom = this.y - this.height;

        double otherLeft = x - width;
        double otherRight = x + width;
        double otherTop = y + height;
        double otherBottom = y - height;

        boolean xOverlap = (thisLeft <= otherRight) && (thisRight >= otherLeft);
        boolean yOverlap = (thisBottom <= otherTop) && (thisTop >= otherBottom);

        return xOverlap && yOverlap;
    }

    public Collection<T> query(double x, double y, double width, double height, Predicate<T> test)
    {
        Collection<T> found = new HashSet<>();

        if (!intersects(x, y, width, height))
        {
            return found;
        }

        if (divided)
        {
            found.addAll(nodeNE.query(x, y, width, height, test));
            found.addAll(nodeSE.query(x, y, width, height, test));
            found.addAll(nodeSW.query(x, y, width, height, test));
            found.addAll(nodeNW.query(x, y, width, height, test));
        } else
        {
            for (Entry<T> entry : entries)
            {
                if (containsPoint(entry.x, entry.y, x, y, width, height) && test.test(entry.obj))
                {
                    found.add(entry.obj());
                }
            }
        }

        return found;
    }

    public Collection<T> query(double x, double y, double width, double height)
    {
        Collection<T> found = new HashSet<>();

        if (!intersects(x, y, width, height))
        {
            return found;
        }

        if (divided)
        {
            found.addAll(nodeNE.query(x, y, width, height));
            found.addAll(nodeSE.query(x, y, width, height));
            found.addAll(nodeSW.query(x, y, width, height));
            found.addAll(nodeNW.query(x, y, width, height));
        } else
        {
            for (Entry<T> entry : entries)
            {
                if (containsPoint(entry.x, entry.y, x, y, width, height))
                {
                    found.add(entry.obj());
                }
            }
        }

        return found;
    }

    private void subdivide()
    {
        double w = width;
        double h = height;

        nodeNE = new QuadTree<>(maxPerLeaf, x + w / 2, y - h / 2, w / 2, h / 2);
        nodeSE = new QuadTree<>(maxPerLeaf, x + w / 2, y + h / 2, w / 2, h / 2);
        nodeSW = new QuadTree<>(maxPerLeaf, x - w / 2, y + h / 2, w / 2, h / 2);
        nodeNW = new QuadTree<>(maxPerLeaf, x - w / 2, y - h / 2, w / 2, h / 2);
        this.divided = true;
    }

    private record Entry<T>(T obj, double x, double y) {}

    public String printDebug(int nestingLevel, String quadrant) 
    {
        // Create an indentation string based on nesting level
        String indentation = " ".repeat(nestingLevel * 4);
    
        // Start building the result string
        StringBuilder result = new StringBuilder(indentation + "QuadTree (" + quadrant + ") {\n");
    
        // Append non-null fields
        if (maxPerLeaf != 0) {
            result.append(indentation).append("    maxPerLeaf=").append(maxPerLeaf).append(",\n");
        }
        if (entries != null) {
            result.append(indentation).append("    entries=").append(entries).append(",\n");
        }
    
        // Similar modifications for other fields...
    
        // Remove the trailing comma and newline if there are any fields
        if (result.charAt(result.length() - 2) == ',') {
            result.delete(result.length() - 2, result.length() - 1);
        }
    
        // Append closing brace with indentation
        result.append(indentation).append("}");
    
        // Recursively append debug information for child nodes if needed
        if (nodeNE != null) {
            result.append("\n").append(nodeNE.printDebug(nestingLevel + 1, "NE"));
        }
        if (nodeSE != null) {
            result.append("\n").append(nodeSE.printDebug(nestingLevel + 1, "SE"));
        }
        if (nodeSW != null) {
            result.append("\n").append(nodeSW.printDebug(nestingLevel + 1, "SW"));
        }
        if (nodeNW != null) {
            result.append("\n").append(nodeNW.printDebug(nestingLevel + 1, "NW"));
        }
    
        return result.toString();
    }
}