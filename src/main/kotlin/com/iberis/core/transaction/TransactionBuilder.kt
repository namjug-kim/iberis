package com.iberis.core.transaction

import com.iberis.core.contract.Contract
import java.security.PrivateKey
import java.security.PublicKey

/**
 * Please describe the role of the TransactionBuilder
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-11
 *
 * @author namjug.kim
 * @since 2018-10-11
 * @version 0.1
 */
class TransactionBuilder(private val privateKey: PrivateKey) {
    private var nonce: Long? = null
    private var sender: PublicKey? = null
    private var contract: Contract? = null

    fun sender(sender: PublicKey): TransactionBuilder {
        this.sender = sender
        return this
    }

    fun contract(contract: Contract): TransactionBuilder {
        this.contract = contract
        return this
    }

    fun nonce(nonce: Long): TransactionBuilder {
        this.nonce = nonce
        return this
    }

    fun build(): Transaction {
        val nonce = checkNotNull(nonce)
        val sender = checkNotNull(sender)
        val contract = checkNotNull(contract)

        val signature = Transaction.generateSignature(privateKey, sender, contract)

        return Transaction(
                nonce = nonce,
                sender = sender,
                signature = signature,
                contractType = contract.contractType(),
                contract = contract
        )
    }
}
