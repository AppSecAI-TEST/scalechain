package io.scalechain.blockchain.api.command.wallet.p0

import io.scalechain.blockchain.api.command.RpcCommand
import io.scalechain.blockchain.api.domain.RpcError
import io.scalechain.blockchain.api.domain.RpcRequest
import io.scalechain.blockchain.api.domain.RpcResult
import io.scalechain.util.Either
import io.scalechain.util.Either.Left
import io.scalechain.util.Either.Right

/*
  CLI command :
    # Spend 0.1 coins to the address below
    # with the comment “sendtoadress example”
    # and the comment-to “Nemo From Example.com”:

    bitcoin-cli -testnet sendtoaddress mmXgiR6KAhZCyQ8ndr2BCfEq1wNG2UnyG6 \
      0.1 "sendtoaddress example" "Nemo From Example.com"

  CLI output :
    a2a2eb18cb051b5fe896a32b1cb20b179d981554b6bd7c5a956e56a0eecb04f0

  Json-RPC request :
    {"jsonrpc": "1.0", "id":"curltest", "method": "sendtoaddress", "params": [] }

  Json-RPC response :
    {
      "result": << Same to CLI Output >> ,
      "error": null,
      "id": "curltest"
    }
*/

/** SendToAddress: spends an amount to a given address.
  *
  * https://bitcoin.org/en/developer-reference#sendtoaddress
  */
object SendToAddress : RpcCommand() {
  override fun invoke(request : RpcRequest) : Either<RpcError, RpcResult?> {
    // TODO : Implement
    assert(false)
    return Right(null)
  }
  override fun help() : String =
"""

"""
}


