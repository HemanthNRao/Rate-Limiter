package com.tuebora.assignment.ratelimiter;

import com.tuebora.assignment.exceptions.RateLimiterException;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;

/**
 * Class to implement leaky bucket rate limiter. With Input and Output types.
 * @param <Input>
 * @param <Output>
 */
public class LeakyBucketRateLimiter<Input, Output> implements RateLimiter<Input, Output> {
    private final int bucketCapacity;
    private final double leakRate;
    private long waterLevel;
    private Instant lastLeakTime;

    public LeakyBucketRateLimiter(int allowedRequests, Duration window)
    {
        this.bucketCapacity = allowedRequests;
        this.leakRate = (double) allowedRequests / window.toMillis();
        this.waterLevel = 0;
        this.lastLeakTime = Instant.now();
    }

    /**
     * Method to wrap any Function F<Input, Output> to apply a rate limit
     * @param function
     * @return Returns function with same signature
     */
    @Override
    public Function<Input, Output> wrap(Function<Input, Output> function)
    {
        return input -> {
            if(isRequestAllowed())
            {
                return function.apply(input);
            }
            else
            {
                throw new RateLimiterException("Rate limit exceeded");
            }
        };
    }

    /**
     * Method to validate is request is allowed or not.
     * Method updates water level according to leak rate from last leak time.
     * if water level is less than capacity then returns true else false.
     * @return
     */
    private boolean isRequestAllowed()
    {
        synchronized(this)
        {
            Instant now = Instant.now();
            long timeSinceLastLeak = (long) (now.toEpochMilli() - lastLeakTime.toEpochMilli() / 1000.0);

            // Leak the water based on the elapsed time since the last leak
            long leakedWater = (long) (timeSinceLastLeak * leakRate);
            waterLevel = Math.max(0, waterLevel - leakedWater);

            if (waterLevel < bucketCapacity)
            {
                waterLevel++;
                lastLeakTime = now;
                return true;
            } else
            {
                return false;
            }
        }
    }
}
