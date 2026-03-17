package com.ledvance.nfc.exception

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/4 19:33
 * Describe : CannotGetAreaIdException
 */
internal class CannotGetAreaIdException :
    RuntimeException("An issue occurred retrieving AreaId from Address, therefore address is probably invalid")