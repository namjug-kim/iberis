package com.iberis.core.merkletree

import com.iberis.crypto.Hash
import io.vavr.collection.Stream

/**
 * Please describe the role of the MerkleTree
 * <B>History:</B>
 * Created by namjug.kim on 2018. 9. 21.
 *
 * @author namjug.kim
 * @since 2018. 9. 21.
 * @version 0.1
 */
class MerkleTree(input: List<Hash>) {
    private var input = input.toList()

    fun root(): Hash {
        return merkle(input)
    }

    tailrec fun merkle(lastNodesList: List<Hash>): Hash {
        return if (lastNodesList.size == 1) {
            lastNodesList[0]
        } else {
            val newLevelHashes = Stream.ofAll(lastNodesList)
                    .sliding(2, 2)
                    .map {
                        val left: Hash = it.get(0)
                        val right: Hash = if (it.size() == 1) left else it.get(1)

                        left.concatHash(right)
                    }
                    .toJavaCollection { ArrayList<Hash>() }

            merkle(newLevelHashes)
        }
    }
}
