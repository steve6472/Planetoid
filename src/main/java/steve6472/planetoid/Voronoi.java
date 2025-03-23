package steve6472.planetoid;

/**
 * @author <a href="https://github.com/maxov/flow-noise/blob/master/src/main/java/com/flowpowered/noise/module/generator/Voronoi.java">...</a>
 */
public class Voronoi
{
    private static final double SQRT_2 = Math.sqrt(2);

    // Frequency of the seed points.
    private final double frequency;
    // Scale of the random displacement to apply to each Voronoi cell.
    private final double displacement;
    // Scale of coordinates
    private final double scale;
    // Determines if the distance from the nearest seed point is applied to
    // the output value.
    private final boolean enableDistance;
    // Seed value used by the coherent-noise function to determine the
    // positions of the seed points.
    private final int seed;

    public Voronoi(double frequency, double displacement, double scale, boolean enableDistance, int seed)
    {
        this.frequency = frequency;
        this.displacement = displacement;
        this.scale = scale;
        this.enableDistance = enableDistance;
        this.seed = seed;
    }

    public double getDisplacement()
    {
        return displacement;
    }

    public boolean isEnableDistance()
    {
        return enableDistance;
    }

    public double getFrequency()
    {
        return frequency;
    }

    public int getSeed()
    {
        return seed;
    }

    public double get(double x, double y)
    {
        double x1 = x * scale;
        double y1 = y * scale;
        // This method could be more efficient by caching the seed values.  Fix
        // later.

        x1 *= frequency;
        y1 *= frequency;

        int xInt = (x1 > 0.0 ? (int) x1 : (int) x1 - 1);
        int yInt = (y1 > 0.0 ? (int) y1 : (int) y1 - 1);

        double minDist = 2147483647.0;
        double xCandidate = 0;
        double yCandidate = 0;

        // Inside each unit cube, there is a seed point at a random position.  Go
        // through each of the nearby cubes until we find a cube with a seed point
        // that is closest to the specified position.
        for (int yCur = yInt - 2; yCur <= yInt + 2; yCur++)
        {
            for (int xCur = xInt - 2; xCur <= xInt + 2; xCur++)
            {

                // Calculate the position and distance to the seed point inside of
                // this unit cube.
                double xPos = xCur + SimplexNoise.noise(xCur, yCur, seed);
                double yPos = yCur + SimplexNoise.noise(xCur, yCur, seed + 1);
                double xDist = xPos - x1;
                double yDist = yPos - y1;
                double dist = xDist * xDist + yDist * yDist;

                if (dist < minDist)
                {
                    // This seed point is closer to any others found so far, so record
                    // this seed point.
                    minDist = dist;
                    xCandidate = xPos;
                    yCandidate = yPos;
                }
            }
        }

        double value;
        if (enableDistance)
        {
            // Determine the distance to the nearest seed point.
            double xDist = xCandidate - x1;
            double yDist = yCandidate - y1;
            value = (Math.sqrt(xDist * xDist + yDist * yDist)) * SQRT_2 - 1.0;
        } else
        {
            value = 0.0;
        }

        // Return the calculated distance with the displacement value applied.
        return value + (displacement * SimplexNoise.noise(Math.floor(xCandidate), Math.floor(yCandidate), seed));
    }

}