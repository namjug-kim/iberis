package com.iberis.account

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Please describe the role of the AccountTest
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-14
 *
 * @author namjug.kim
 * @version 0.1
 * @since 2018-10-14
 */
internal class AccountTest {
    @Test
    fun `public key serialize deserialize`() {
        val account = Account.createAccount()

        val fromByteArray = AccountId.fromByteArray(account.publicKey.encoded)

        assertEquals(account.accountId.address, fromByteArray.address)
    }
}
