package com.tuebora.assignment.exceptions;

/**
 * Custom exception for Rate Limiter
 */
public class RateLimiterException extends RuntimeException
{
    public RateLimiterException(String message)
    {
        super(message);
    }
}
