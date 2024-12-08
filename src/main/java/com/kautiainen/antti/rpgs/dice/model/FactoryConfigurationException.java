package com.kautiainen.antti.rpgs.dice.model;

/**
 * A factory configuration exception.
 */
public abstract class FactoryConfigurationException extends RuntimeException {

    /**
     * The invalid configuraiton key.
     */
    protected final String configKey;

    /**
     * Create a new factory configuraiton exception.
     * 
     * @param configKey The invalid configuration key.
     * @param message The message of the excpetion.
     * @param cause The cause of the exception.
     */
    public FactoryConfigurationException(String configKey, String message, Throwable cause) {
        super(message, cause);
        this.configKey = configKey;
    }

    /**
     * Create a new factory configuration exception.
     * 
     * @param configKey The invalid configuration key.
     * @param message The message of the excpetion.
     */
    public FactoryConfigurationException(String configKey, String message) {
        this(configKey, message, (Throwable)null);
    }
}