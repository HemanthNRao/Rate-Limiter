package com.tuebora.assignment.ratelimiter;

import com.tuebora.assignment.exceptions.RateLimiterException;

import java.time.Duration;
import java.time.Instant;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;

/**
 * Class to implement sliding window rate limiter. With Input and Output types.
 * @param <Input>
 * @param <Output>
 */
public class SlidingWindowRateLimiter<Input, Output> implements RateLimiter<Input, Output>
{
    private final int allowedRate;
    private final Duration window;
    private final Queue<Instant> lastAccessList;

    public SlidingWindowRateLimiter(int allowedRate, Duration window)
    {
        this.allowedRate = allowedRate;
        this.window = window;
        this.lastAccessList = new ConcurrentLinkedQueue<>();
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
            updateAccessList();
            if(!isRequestAllowed())
            {
                throw new RateLimiterException("Rate limit exceeded");
            }
            else
            {
                return function.apply(input);
            }
        };
    }

    /**
     * Method to validate is request is allowed or not.
     * Method counts number of request arrived in given window. If value is
     * greater than allowedRate then returns false else true
     * @return
     */
    private boolean isRequestAllowed()
    {
        if(lastAccessList.size() >= allowedRate)
            return false;
        lastAccessList.add(Instant.now());
        return true;
    }

    /**
     * Method to remove entries which are older than current sliding window.
     */
    private void updateAccessList()
    {
        Instant now = Instant.now();
        Instant windowStart = now.minus(window);
        while (!lastAccessList.isEmpty() && lastAccessList.peek().isBefore(windowStart))
        {
            lastAccessList.poll();
        }
    }
}