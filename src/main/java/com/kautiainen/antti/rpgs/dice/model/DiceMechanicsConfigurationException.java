package com.kautiainen.antti.rpgs.dice.model;

/**
 * An exception indicating a dice mechanics configuration was invalid.
 */
public class DiceMechanicsConfigurationException extends FactoryConfigurationException {

    /**
     * Get the default key of the dice mechanics configuration error.
     * 
     * @return The default key of the dice mechancis.
     */
    public static String defaultKey() {
        return "dice-mechanics";
    }

    public DiceMechanicsConfigurationException(String configKey, String message, Throwable cause) {
        super(configKey, message, cause);
    }

    public DiceMechanicsConfigurationException(String message) {
        this(defaultKey(), message, null);
    }
}