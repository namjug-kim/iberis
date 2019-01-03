package com.iberis.core.block

import com.iberis.core.merkletree.MerkleTree
import com.iberis.util.hexToByteArray
import org.junit.Assert
import org.junit.Test

/**
 * Please describe the role of the MerkleTreeTest
 * <B>History:</B>
 * Created by namjug.kim on 2018-10-11
 *
 * @author namjug.kim
 * @version 0.1
 * @since 2018-10-11
 */
internal class MerkleTreeTest {
    @Test
    fun `merkle root bitcoin height 100000`() {

        val transactions = arrayOf("8c14f0db3df150123e6f3dbbf30f8b955a8249b62ac1d1ff16284aefa3d06d87",
                "fff2525b8931402dd09222c50775608f75787bd2b87e56995a7bdd30f79702c4",
                "6359f0868171b1d194cbee1af2f16ea598ae8fad666d9b012c8ed2b79a236ec4",
                "e9a66845e05d5abc0ad04ec80f774a7e585c6e8db975962d069a522137b80c1d")
                .map { it.hexToByteArray() }
                .map { com.iberis.crypto.Hash(it) }
                .toList()

        val merkleTree = MerkleTree(transactions)
        val root = merkleTree.root()
        Assert.assertEquals("F3E94742ACA4B5EF85488DC37C06C3282295FFEC960994B2C0D5AC2A25A95766", root.hex)
    }

    @Test
    fun `merkle root bitcoin height 99997`() {

        val transactions = arrayOf("b86f5ef1da8ddbdb29ec269b535810ee61289eeac7bf2b2523b494551f03897c",
                "80c6f121c3e9fe0a59177e49874d8c703cbadee0700a782e4002e87d862373c6")
                .map { it.hexToByteArray() }
                .map { com.iberis.crypto.Hash(it) }
                .toList()

        val merkleTree = MerkleTree(transactions)
        val root = merkleTree.root()

        Assert.assertEquals("5140E5972F672BF8E81BC189894C55A410723B095716EAEEC845490AED785F0E", root.hex)
    }

    @Test
    fun `merkle root odd numbers of transaction bitcoin height 99998`() {

        val transactions = arrayOf("f3a0ad3b0325b74919b8f24a797619a26fec7ea8ef67bea2fccbc99674b2ab0a")
                .map { it.hexToByteArray() }
                .map { com.iberis.crypto.Hash(it) }
                .toList()

        val merkleTree = MerkleTree(transactions)
        val root = merkleTree.root()

        Assert.assertEquals("F3A0AD3B0325B74919B8F24A797619A26FEC7EA8EF67BEA2FCCBC99674B2AB0A", root.hex)
    }
}
