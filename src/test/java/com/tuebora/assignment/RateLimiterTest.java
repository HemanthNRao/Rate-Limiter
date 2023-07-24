package com.tuebora.assignment;

import com.tuebora.assignment.exceptions.RateLimiterException;
import com.tuebora.assignment.ratelimiter.LeakyBucketRateLimiter;
import com.tuebora.assignment.ratelimiter.RateLimiter;
import com.tuebora.assignment.ratelimiter.SlidingWindowRateLimiter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.function.Function;

public class RateLimiterTest
{
    /**
     * Target method to wrap.
     * Method takes integer and returns square of it
     * @param x
     * @return
     */
    private int square(int x)
    {
        return x*x;
    }

    Function<Integer, Integer> squareFunction = this::square;

    /**
     * Testcase: Unit test to check
     * Sliding window rate limiter will accept if number of requests is less than allowed rate.
     * Rate limiter is configured to serve 10 requests within 10 seconds.
     */
    @Test
    public void handle10RequestsIn10SecsTest()
    {
        RateLimiter<Integer, Integer> rateLimiter = new SlidingWindowRateLimiter<>(10, Duration.ofSeconds(10));
        Function<Integer, Integer> f = rateLimiter.wrap(squareFunction);
        for(int i=1;i<=10;i++)
        {
            f.apply(i);
        }
    }

    /**
     * Testcase: Unit test to check
     * Leaky bucket rate limiter will accept if number of requests is less than allowed rate.
     * Rate limiter is configured to serve 10 requests within 10 seconds.
     */
    @Test
    public void handle10RequestsIn10SecsLeakyBucketTest()
    {
        RateLimiter<Integer, Integer> rateLimiter = new LeakyBucketRateLimiter<Integer, Integer>(10, Duration.ofSeconds(10));
        Function<Integer, Integer> f = rateLimiter.wrap(squareFunction);
        for(int i=1;i<=10;i++)
        {
            f.apply(i);
        }
    }

    /**
     * Testcase: Unit test to check
     * Sliding window rate limiter will throw exception if number of requests is more than allowed rate.
     * Rate limiter is configured to serve 2 requests within 5 seconds.
     */
    @Test
    public void ThrowExceptionIfMoreThan2RequestsIn5SecsTest()
    {
        RateLimiter<Integer, Integer> rateLimiter = new SlidingWindowRateLimiter<>(2, Duration.ofSeconds(5));
        Function<Integer, Integer> f = rateLimiter.wrap(squareFunction);
        for(int i=1;i<=2;i++)
        {
            f.apply(i);
        }
        Assertions.assertThrows(RateLimiterException.class, () -> f.apply(10));
    }

    /**
     * Testcase: Unit test to check
     * Leaky bucket rate limiter will throw exception if number of requests is more than allowed rate.
     * Rate limiter is configured to serve 2 requests within 5 seconds.
     */
    @Test
    public void ThrowExceptionIfMoreThan2RequestsIn5SecsTestLeakyBucket()
    {
        RateLimiter<Integer, Integer> rateLimiter = new LeakyBucketRateLimiter<>(2, Duration.ofSeconds(5));
        Function<Integer, Integer> f = rateLimiter.wrap(squareFunction);
        for(int i=1;i<=2;i++)
        {
            f.apply(i);
        }
        Assertions.assertThrows(RateLimiterException.class, () -> f.apply(10));
    }

}
