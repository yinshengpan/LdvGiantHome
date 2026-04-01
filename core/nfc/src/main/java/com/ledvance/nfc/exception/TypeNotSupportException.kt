package com.ledvance.nfc.exception

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/4 19:32
 * Describe : TypeNotSupportException
 */
internal class TypeNotSupportException(typeName: String) :
    RuntimeException("The type of using tag $typeName is not supported yet")