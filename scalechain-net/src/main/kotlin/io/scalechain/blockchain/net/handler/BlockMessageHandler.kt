package io.scalechain.blockchain.net.handler

import io.scalechain.blockchain.net.Node
import io.scalechain.blockchain.net.BlockGateway
import io.scalechain.blockchain.chain.Blockchain
import io.scalechain.blockchain.proto.*
import io.scalechain.util.Config
import org.slf4j.LoggerFactory
import io.scalechain.blockchain.script.hash

/**
  * The message handler for Block message.
  */
object BlockMessageHandler {
  private val logger = LoggerFactory.getLogger(BlockMessageHandler.javaClass)

  // More than half of the peers should sign the block.
  val RequiredSigningTransactions = Config.peerAddresses().size / 2 + 1


  /** Handle Block message.
    *
    * @param context The context where handlers handling different messages for a peer can use to store state data.
    * @param block The Block message to handle.
    * @return Some(message) if we need to respond to the peer with the message.
    */
  fun handle( context : MessageHandlerContext, block : Block ) : Unit {

    // TODO : BUGBUG : Need to think about RocksDB transactions.

    val blockHash = block.header.hash()
    processNewBlock(blockHash, block)

    logger.trace("<P2P> Received a block. Hash : ${blockHash}, Header : ${block.header}")
  }

  fun processNewBlock(blockHash : Hash, block:Block) : Unit {
    val node = Node.get()
    val processedByIBD = synchronized(node) {
      if ( node.isInitialBlockDownload() ) {
        val peerBestHeight = node.bestPeerStartHeight()
        if ( peerBestHeight > Blockchain.get().getBestBlockHeight() ) {
          // putBlock returns true if the block was processed by it.
          BlockGateway.putBlock(blockHash, block)
        } else {
          false
        }
      } else {
        false
      }
    }

    if (!processedByIBD) { // putReceivedBlock processes the block only if it was not processed by the IBD.
      BlockGateway.putReceivedBlock(blockHash, block)
    }
/*
    if (chain.getBestBlockHash() == )

      // TODO : Add the block as a known inventory of the node that sent it.
      // pfrom->AddInventoryKnown(inv);
      BlockProcessor.validateBlock(block)
      // TODO : Increase DoS score of the peer if the block was invalid.
      // TODO : Make sure we do not hit any issue even though an exception is thrown by validateBlock.

      if (BlockProcessor.hasNonOrphan(block.header.hashPrevBlock)) { // Case 1 : Non-Orphan block.
        // Step 1.1 : Store the block to the blockchain. Do block reorganization if necessary.
        val acceptedAsNewBestBlock = BlockProcessor.acceptBlock(blockHash, block)

        // Step 1.2 : Recursively bring any orphan blocks to blockchain, if the new block is the previous block of any orphan block.
        val newlyBestBlockHashes : List<Hash> = BlockProcessor.acceptChildren(blockHash)

        val newBlockHashes = if (acceptedAsNewBestBlock) {
          blockHash :: newlyBestBlockHashes
        } else {
          newlyBestBlockHashes
        }

        if (newBlockHashes.isEmpty) {
          // Do nothing. Nothing to send.
          logger.info(s"A block was ignored, as there is an orphan block being requested. ${blockHash}")
        } else {
          // Step 1.3 : Relay the newly added blocks to peers as an inventory
          val invMessage = InvFactory.createBlockInventories(newBlockHashes)
          context.communicator.sendToAll(invMessage)
          logger.trace(s"Propagating newly accepted blocks : ${invMessage} ")
        }
      } else { // Case 2 : Orphan block

/*
        if (context.peer.requestedBlock().isDefined ) {
          // Do not process orphan blocks. We already requested parents of an orphan root.
          logger.info(s"A block was ignored, as there is an orphan block being requested. ${blockHash}")
        } else {
          // Step 2.1 : Put the orphan block
          // BUGBUG : An attacker can fill up my disk with lots of orphan blocks.
          BlockProcessor.putOrphan(block)

          // Step 2.2 : Request the peer to send the root parent of the orphan block.
          val orphanRootHash : Hash = BlockProcessor.getOrphanRoot(blockHash)

          val getBlocksMessage = GetBlocksFactory.create(orphanRootHash)
          context.peer.send(getBlocksMessage)
          context.peer.blockRequested(orphanRootHash)
          logger.info(s"An orphan block was found. Block Hash : ${blockHash}, Previous Hash : ${block.header.hashPrevBlock}, Inventories requested : ${getBlocksMessage} ")
          logger.trace(s"Requesting inventories of parents of the orphan. Orphan root: ${orphanRootHash} ")
        }
*/
      }
    }

    if ( context.peer.requestedBlock().isDefined ) { // If the block hash matches the requested block, clear the requested block for getting parents of an orphan root.
      if ( blockHash == context.peer.requestedBlock().get ) {
        logger.trace(s"The requested block received. Clearing the requested block. ${blockHash}")
        context.peer.clearRequestedBlock()
      }
    }
*/

  }
}
