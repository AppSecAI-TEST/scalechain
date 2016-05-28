package io.scalechain.blockchain.transaction

import io.scalechain.blockchain.proto._
import io.scalechain.blockchain.script.ops.OpPush
import io.scalechain.blockchain.script.{HashCalculator, ScriptParser, BlockPrinterSetter}
import io.scalechain.util.HexUtil._
import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest.prop.Tables.Table
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Suite}

/** Test signature validation operations in Crypto.scala
  *
  */
class SignatureCheckingSpec extends FlatSpec with BeforeAndAfterEach with SignatureTestTrait {

  this: Suite =>

  override def beforeEach() {
    // set-up code
    //

    super.beforeEach()
  }

  override def afterEach() {
    super.afterEach()
    // tear-down code
    //
  }

  val transactionInputs =
    Table(
      // column names
      ("subject", "mergedScript"),

      // Summarize the locking/unlocking script as a subject.
      // The MergedScript creating code was copied from the output of DumpChain merge-scripts,
      // which reads all transactions from blkNNNNN.dat file written by the reference implementation.
      (
        "p2pk",
        MergedScript(transaction=Transaction(version=1, inputs=List(NormalTransactionInput(outputTransactionHash=Hash(bytes("8908c26fe156c326f0b032a47b88b53bb94e64890fbfc1ce40d484fa3839f40d")), outputIndex=0L, unlockingScript=UnlockingScript(bytes("483045022100dc870fd001c3192dbc14a0ddd98cf23aa760ff9923d103c9c2feaf8f2d00bed60220499897a167726d84c064aceb6f9384641368df536886dcbb8106124dfdc8ccde01")) /* ops:ScriptOpList(operations=Array(OpPush(72,ScriptBytes(bytes("3045022100dc870fd001c3192dbc14a0ddd98cf23aa760ff9923d103c9c2feaf8f2d00bed60220499897a167726d84c064aceb6f9384641368df536886dcbb8106124dfdc8ccde01"))))), hashType:Some(1) */, sequenceNumber=4294967295L),NormalTransactionInput(outputTransactionHash=Hash(bytes("115f940a1363ca9ab303fdd3fdddb8d6d0d98fbfc013419fc66917b2bded5208")), outputIndex=0L, unlockingScript=UnlockingScript(bytes("493046022100ca1610c6d372e37467671bebe77b722fac72044aef22cc32788d399726aafaa7022100af4677e2f214ecec85b207b99bee9417697a66ded3aa775a99df90157689c5f501")) /* ops:ScriptOpList(operations=Array(OpPush(73,ScriptBytes(bytes("3046022100ca1610c6d372e37467671bebe77b722fac72044aef22cc32788d399726aafaa7022100af4677e2f214ecec85b207b99bee9417697a66ded3aa775a99df90157689c5f501"))))), hashType:Some(1) */, sequenceNumber=4294967295L),NormalTransactionInput(outputTransactionHash=Hash(bytes("cde5906b01c1b4247709d5aff006133fb227a764cd89f8507b5d1114338150f2")), outputIndex=0L, unlockingScript=UnlockingScript(bytes("483045022017ddfc6b146311a5510bf3a00008e25427254e54094646e5549a08b3cba5e836022100c64a8a4a94370a74ec3d9dcddd8d31df37934a9237e64071c10adccacbcb3eea01")) /* ops:ScriptOpList(operations=Array(OpPush(72,ScriptBytes(bytes("3045022017ddfc6b146311a5510bf3a00008e25427254e54094646e5549a08b3cba5e836022100c64a8a4a94370a74ec3d9dcddd8d31df37934a9237e64071c10adccacbcb3eea01"))))), hashType:Some(1) */, sequenceNumber=4294967295L)), outputs=List(TransactionOutput(value=15000000000L, lockingScript=LockingScript(bytes("4104e4072d4a04889ebfdb48165f7cb0ad5fd586690986c5b2297462955f7d0650b93ab666382292438acc94e07570197dcbaed0f4652b17b800f8e25ece51e243bbac")) /* ops:ScriptOpList(operations=Array(OpPush(65,ScriptBytes(bytes("04e4072d4a04889ebfdb48165f7cb0ad5fd586690986c5b2297462955f7d0650b93ab666382292438acc94e07570197dcbaed0f4652b17b800f8e25ece51e243bb"))),OpCheckSig(Script(bytes("4104e4072d4a04889ebfdb48165f7cb0ad5fd586690986c5b2297462955f7d0650b93ab666382292438acc94e07570197dcbaed0f4652b17b800f8e25ece51e243bbac"))))) */ )), lockTime=0L /* hash:bytes("47e582127b119aca9ef4a21c9fa17d00bd86847440137c0f5585503286758a90") */), inputIndex=1, unlockingScript=UnlockingScript(bytes("493046022100ca1610c6d372e37467671bebe77b722fac72044aef22cc32788d399726aafaa7022100af4677e2f214ecec85b207b99bee9417697a66ded3aa775a99df90157689c5f501")) /* ops:ScriptOpList(operations=Array(OpPush(73,ScriptBytes(bytes("3046022100ca1610c6d372e37467671bebe77b722fac72044aef22cc32788d399726aafaa7022100af4677e2f214ecec85b207b99bee9417697a66ded3aa775a99df90157689c5f501"))))), hashType:Some(1) */, lockingScript=LockingScript(bytes("4104772c3ad9afd20d65a7b05c56e8ebd002abed58c49ec5874c6e6eef4a292f5df13998fc05b6b8dc63447e028dd4bbc1c05b872f8527d2b1057e47831028223c3fac")) /* ops:ScriptOpList(operations=Array(OpPush(65,ScriptBytes(bytes("04772c3ad9afd20d65a7b05c56e8ebd002abed58c49ec5874c6e6eef4a292f5df13998fc05b6b8dc63447e028dd4bbc1c05b872f8527d2b1057e47831028223c3f"))),OpCheckSig(Script(bytes("4104772c3ad9afd20d65a7b05c56e8ebd002abed58c49ec5874c6e6eef4a292f5df13998fc05b6b8dc63447e028dd4bbc1c05b872f8527d2b1057e47831028223c3fac"))))) */ )
      ),
      (
        "p2pkh uncompressed public key",
        MergedScript(transaction=Transaction(version=1, inputs=List(NormalTransactionInput(outputTransactionHash=Hash(bytes("789ef6ceb7aa05bd6340640bfb80a14f533258963e0bc988aab0f0621064201c")), outputIndex=1L, unlockingScript=UnlockingScript(bytes("483045022100e226e68f61719cc8104c496b0f6e7ea842e2475272d046b3a78e62d00986570f02201643c7bf45ab44ad4dc1c151451b9808c82666c3410b7934f28e6ded0104d57501410409cd755221d250896fcadb560e21e4ee72a3431dfcf16e348dae6ed731d9fb9e5d3de97622c3616031723fe7b930adcdf43e2a08ca60475d865b3f9e83bfc0bb")) /* ops:ScriptOpList(operations=Array(OpPush(72,ScriptBytes(bytes("3045022100e226e68f61719cc8104c496b0f6e7ea842e2475272d046b3a78e62d00986570f02201643c7bf45ab44ad4dc1c151451b9808c82666c3410b7934f28e6ded0104d57501"))),OpPush(65,ScriptBytes(bytes("0409cd755221d250896fcadb560e21e4ee72a3431dfcf16e348dae6ed731d9fb9e5d3de97622c3616031723fe7b930adcdf43e2a08ca60475d865b3f9e83bfc0bb"))))), hashType:Some(1) */, sequenceNumber=4294967295L)), outputs=List(TransactionOutput(value=2100000L, lockingScript=LockingScript(bytes("76a91478ba39266a577f43cf47c07b306636f534beb3ee88ac")) /* ops:ScriptOpList(operations=Array(OpDup(),OpHash160(),OpPush(20,ScriptBytes(bytes("78ba39266a577f43cf47c07b306636f534beb3ee"))),OpEqualVerify(),OpCheckSig(Script(bytes("76a91478ba39266a577f43cf47c07b306636f534beb3ee88ac"))))) */ ),TransactionOutput(value=123240000L, lockingScript=LockingScript(bytes("76a9143f59f840d8c9232a6a8794a9c0357060820149a088ac")) /* ops:ScriptOpList(operations=Array(OpDup(),OpHash160(),OpPush(20,ScriptBytes(bytes("3f59f840d8c9232a6a8794a9c0357060820149a0"))),OpEqualVerify(),OpCheckSig(Script(bytes("76a9143f59f840d8c9232a6a8794a9c0357060820149a088ac"))))) */ )), lockTime=0L /* hash:bytes("23c0a7efe25ac7c948d57389f1b1579213abb27b3deea00a27b41d796010d02b") */), inputIndex=0, unlockingScript=UnlockingScript(bytes("483045022100e226e68f61719cc8104c496b0f6e7ea842e2475272d046b3a78e62d00986570f02201643c7bf45ab44ad4dc1c151451b9808c82666c3410b7934f28e6ded0104d57501410409cd755221d250896fcadb560e21e4ee72a3431dfcf16e348dae6ed731d9fb9e5d3de97622c3616031723fe7b930adcdf43e2a08ca60475d865b3f9e83bfc0bb")) /* ops:ScriptOpList(operations=Array(OpPush(72,ScriptBytes(bytes("3045022100e226e68f61719cc8104c496b0f6e7ea842e2475272d046b3a78e62d00986570f02201643c7bf45ab44ad4dc1c151451b9808c82666c3410b7934f28e6ded0104d57501"))),OpPush(65,ScriptBytes(bytes("0409cd755221d250896fcadb560e21e4ee72a3431dfcf16e348dae6ed731d9fb9e5d3de97622c3616031723fe7b930adcdf43e2a08ca60475d865b3f9e83bfc0bb"))))), hashType:Some(1) */, lockingScript=LockingScript(bytes("76a9143f59f840d8c9232a6a8794a9c0357060820149a088ac")) /* ops:ScriptOpList(operations=Array(OpDup(),OpHash160(),OpPush(20,ScriptBytes(bytes("3f59f840d8c9232a6a8794a9c0357060820149a0"))),OpEqualVerify(),OpCheckSig(Script(bytes("76a9143f59f840d8c9232a6a8794a9c0357060820149a088ac"))))) */ )
      ),
      (
        "p2pkh with compressed public key",
        // Caution : Do not edit this line. This line is copied from the output of DumpChain program.
        MergedScript(transaction=Transaction(version=1, inputs=List(NormalTransactionInput(outputTransactionHash=Hash(bytes("12f7eb67cbfa1df30cf3f60833a41e9d50eeb658aff7e410eeed21172afb5a7b")), outputIndex=2L, unlockingScript=UnlockingScript(bytes("4830450221008f70e4947b1dc666cb6b8296ebb7a9cca3d7eb55740213c7c19673dd9b1b66f40220634bc7e806fbca468aeade5da23160770188582b218174ed91c0bb619771cbc50121031c24239a829a89d7e12a0a5b1456ce60168c2c7dd29b63ea6a2aa8ef64665050")) /* ops:ScriptOpList(operations=Array(OpPush(72,ScriptBytes(bytes("30450221008f70e4947b1dc666cb6b8296ebb7a9cca3d7eb55740213c7c19673dd9b1b66f40220634bc7e806fbca468aeade5da23160770188582b218174ed91c0bb619771cbc501"))),OpPush(33,ScriptBytes(bytes("031c24239a829a89d7e12a0a5b1456ce60168c2c7dd29b63ea6a2aa8ef64665050"))))), hashType:Some(1) */, sequenceNumber=4294967295L)), outputs=List(TransactionOutput(value=100000000L, lockingScript=LockingScript(bytes("76a91426b8b5d8bc8548c5176d1d8e9046320dc35d8ff588ac")) /* ops:ScriptOpList(operations=Array(OpDup(),OpHash160(),OpPush(20,ScriptBytes(bytes("26b8b5d8bc8548c5176d1d8e9046320dc35d8ff5"))),OpEqualVerify(),OpCheckSig(Script(bytes("76a91426b8b5d8bc8548c5176d1d8e9046320dc35d8ff588ac"))))) */ ),TransactionOutput(value=4504811438L, lockingScript=LockingScript(bytes("76a914425d9c60c16e8364c2125f2560c8d3e847c0827988ac")) /* ops:ScriptOpList(operations=Array(OpDup(),OpHash160(),OpPush(20,ScriptBytes(bytes("425d9c60c16e8364c2125f2560c8d3e847c08279"))),OpEqualVerify(),OpCheckSig(Script(bytes("76a914425d9c60c16e8364c2125f2560c8d3e847c0827988ac"))))) */ ),TransactionOutput(value=500000000L, lockingScript=LockingScript(bytes("76a914e55ce4ca624664c18f66165780eeb36ee68fbb7888ac")) /* ops:ScriptOpList(operations=Array(OpDup(),OpHash160(),OpPush(20,ScriptBytes(bytes("e55ce4ca624664c18f66165780eeb36ee68fbb78"))),OpEqualVerify(),OpCheckSig(Script(bytes("76a914e55ce4ca624664c18f66165780eeb36ee68fbb7888ac"))))) */ )), lockTime=0L /* hash:bytes("b0f8d98ac85f74fa8053c1ae7eb7420048bde825824e327bfa6e9456cfac5b78") */), inputIndex=0, unlockingScript=UnlockingScript(bytes("4830450221008f70e4947b1dc666cb6b8296ebb7a9cca3d7eb55740213c7c19673dd9b1b66f40220634bc7e806fbca468aeade5da23160770188582b218174ed91c0bb619771cbc50121031c24239a829a89d7e12a0a5b1456ce60168c2c7dd29b63ea6a2aa8ef64665050")) /* ops:ScriptOpList(operations=Array(OpPush(72,ScriptBytes(bytes("30450221008f70e4947b1dc666cb6b8296ebb7a9cca3d7eb55740213c7c19673dd9b1b66f40220634bc7e806fbca468aeade5da23160770188582b218174ed91c0bb619771cbc501"))),OpPush(33,ScriptBytes(bytes("031c24239a829a89d7e12a0a5b1456ce60168c2c7dd29b63ea6a2aa8ef64665050"))))), hashType:Some(1) */, lockingScript=LockingScript(bytes("76a914425d9c60c16e8364c2125f2560c8d3e847c0827988ac")) /* ops:ScriptOpList(operations=Array(OpDup(),OpHash160(),OpPush(20,ScriptBytes(bytes("425d9c60c16e8364c2125f2560c8d3e847c08279"))),OpEqualVerify(),OpCheckSig(Script(bytes("76a914425d9c60c16e8364c2125f2560c8d3e847c0827988ac"))))) */ )
      ),
      (
        "checkmultisig without p2sh : 2 of 2",
        // Caution : Do not edit this line. This line is copied from the output of DumpChain program.
        MergedScript(transaction=Transaction(version=1, inputs=List(NormalTransactionInput(outputTransactionHash=Hash(bytes("592ed48d2fa14c9901345607f3a4e769acc3480685f9ed0df24676a538dee396")), outputIndex=0L, unlockingScript=UnlockingScript(bytes("00483045022100c07ac842dde7cd326ea3c72d33df6e8d2a7af685755343fb270a187d6b6b260c022039fcff7bd36b3abea9273383141af01b02a502ec362fe93cdd28cfdf9606dd1c01483045022100831f1b5f7842fb130ccaff89855bcf402351fd4b103fc9cbbf07b6576035bcb90220294ee3f8c91dbeec7888fccf315170a61755f36290576ada9b45b748591d098201")) /* ops:ScriptOpList(operations=Array(Op0(),OpPush(72,ScriptBytes(bytes("3045022100c07ac842dde7cd326ea3c72d33df6e8d2a7af685755343fb270a187d6b6b260c022039fcff7bd36b3abea9273383141af01b02a502ec362fe93cdd28cfdf9606dd1c01"))),OpPush(72,ScriptBytes(bytes("3045022100831f1b5f7842fb130ccaff89855bcf402351fd4b103fc9cbbf07b6576035bcb90220294ee3f8c91dbeec7888fccf315170a61755f36290576ada9b45b748591d098201"))))), hashType:None */, sequenceNumber=4294967295L),NormalTransactionInput(outputTransactionHash=Hash(bytes("b85d14d18cacd48141707ac0007a5e30be702282b21167daf955141e6b02aa8f")), outputIndex=0L, unlockingScript=UnlockingScript(bytes("00483045022100fd28e743c3ff43b4e235e82db688295ae026761f2086312fe0071c4eb6019377022033af7bd246b110e5f2ee7b58184d5fd1ba9f4fc5a47501fc99a2160085ba7360014730440220367b68dd7c163d16a041cd5106230e0289cb68d74840089a857cf14aac8574b702204abbc8602b94b308145569e053e25a5091b2032000961cf5325b5bc6e12131db01")) /* ops:ScriptOpList(operations=Array(Op0(),OpPush(72,ScriptBytes(bytes("3045022100fd28e743c3ff43b4e235e82db688295ae026761f2086312fe0071c4eb6019377022033af7bd246b110e5f2ee7b58184d5fd1ba9f4fc5a47501fc99a2160085ba736001"))),OpPush(71,ScriptBytes(bytes("30440220367b68dd7c163d16a041cd5106230e0289cb68d74840089a857cf14aac8574b702204abbc8602b94b308145569e053e25a5091b2032000961cf5325b5bc6e12131db01"))))), hashType:None */, sequenceNumber=4294967295L)), outputs=List(TransactionOutput(value=7800L, lockingScript=LockingScript(bytes("522103490c610d5e29a62a905cc3c97225ac1ca56e8677bef2b46e71adc045583b3dc62103e3e869a441cd5646e14d6aecd050545409eb20f069d5a73a3a110cdf11e192f652ae")) /* ops:ScriptOpList(operations=Array(OpNum(2),OpPush(33,ScriptBytes(bytes("03490c610d5e29a62a905cc3c97225ac1ca56e8677bef2b46e71adc045583b3dc6"))),OpPush(33,ScriptBytes(bytes("03e3e869a441cd5646e14d6aecd050545409eb20f069d5a73a3a110cdf11e192f6"))),OpNum(2),OpCheckMultiSig(Script(bytes("522103490c610d5e29a62a905cc3c97225ac1ca56e8677bef2b46e71adc045583b3dc62103e3e869a441cd5646e14d6aecd050545409eb20f069d5a73a3a110cdf11e192f652ae"))))) */ ),TransactionOutput(value=7800L, lockingScript=LockingScript(bytes("512103dd1515a32cb25a77370541c81923e1d0813c98eec4b86fa35c1644b97152ab1a2103f76963cd546578960ae2312cc10d41a0251cc02f8277eb645864c3dfbb3f0af7210229cc3dda58b33c1c83a69112a6c960e78f723f792ca03f7f1e1ce021b640573253ae")) /* ops:ScriptOpList(operations=Array(Op1(),OpPush(33,ScriptBytes(bytes("03dd1515a32cb25a77370541c81923e1d0813c98eec4b86fa35c1644b97152ab1a"))),OpPush(33,ScriptBytes(bytes("03f76963cd546578960ae2312cc10d41a0251cc02f8277eb645864c3dfbb3f0af7"))),OpPush(33,ScriptBytes(bytes("0229cc3dda58b33c1c83a69112a6c960e78f723f792ca03f7f1e1ce021b6405732"))),OpNum(3),OpCheckMultiSig(Script(bytes("512103dd1515a32cb25a77370541c81923e1d0813c98eec4b86fa35c1644b97152ab1a2103f76963cd546578960ae2312cc10d41a0251cc02f8277eb645864c3dfbb3f0af7210229cc3dda58b33c1c83a69112a6c960e78f723f792ca03f7f1e1ce021b640573253ae"))))) */ ),TransactionOutput(value=10000L, lockingScript=LockingScript(bytes("52210229cc3dda58b33c1c83a69112a6c960e78f723f792ca03f7f1e1ce021b64057322103490c610d5e29a62a905cc3c97225ac1ca56e8677bef2b46e71adc045583b3dc652ae")) /* ops:ScriptOpList(operations=Array(OpNum(2),OpPush(33,ScriptBytes(bytes("0229cc3dda58b33c1c83a69112a6c960e78f723f792ca03f7f1e1ce021b6405732"))),OpPush(33,ScriptBytes(bytes("03490c610d5e29a62a905cc3c97225ac1ca56e8677bef2b46e71adc045583b3dc6"))),OpNum(2),OpCheckMultiSig(Script(bytes("52210229cc3dda58b33c1c83a69112a6c960e78f723f792ca03f7f1e1ce021b64057322103490c610d5e29a62a905cc3c97225ac1ca56e8677bef2b46e71adc045583b3dc652ae"))))) */ )), lockTime=0L /* hash:bytes("7e3bf3170ccb719b3170c1c5fb4ac51b631336bb599a061b696a231bff11b12f") */), inputIndex=0, unlockingScript=UnlockingScript(bytes("00483045022100c07ac842dde7cd326ea3c72d33df6e8d2a7af685755343fb270a187d6b6b260c022039fcff7bd36b3abea9273383141af01b02a502ec362fe93cdd28cfdf9606dd1c01483045022100831f1b5f7842fb130ccaff89855bcf402351fd4b103fc9cbbf07b6576035bcb90220294ee3f8c91dbeec7888fccf315170a61755f36290576ada9b45b748591d098201")) /* ops:ScriptOpList(operations=Array(Op0(),OpPush(72,ScriptBytes(bytes("3045022100c07ac842dde7cd326ea3c72d33df6e8d2a7af685755343fb270a187d6b6b260c022039fcff7bd36b3abea9273383141af01b02a502ec362fe93cdd28cfdf9606dd1c01"))),OpPush(72,ScriptBytes(bytes("3045022100831f1b5f7842fb130ccaff89855bcf402351fd4b103fc9cbbf07b6576035bcb90220294ee3f8c91dbeec7888fccf315170a61755f36290576ada9b45b748591d098201"))))), hashType:None */, lockingScript=LockingScript(bytes("52210229cc3dda58b33c1c83a69112a6c960e78f723f792ca03f7f1e1ce021b64057322103490c610d5e29a62a905cc3c97225ac1ca56e8677bef2b46e71adc045583b3dc652ae")) /* ops:ScriptOpList(operations=Array(OpNum(2),OpPush(33,ScriptBytes(bytes("0229cc3dda58b33c1c83a69112a6c960e78f723f792ca03f7f1e1ce021b6405732"))),OpPush(33,ScriptBytes(bytes("03490c610d5e29a62a905cc3c97225ac1ca56e8677bef2b46e71adc045583b3dc6"))),OpNum(2),OpCheckMultiSig(Script(bytes("52210229cc3dda58b33c1c83a69112a6c960e78f723f792ca03f7f1e1ce021b64057322103490c610d5e29a62a905cc3c97225ac1ca56e8677bef2b46e71adc045583b3dc652ae"))))) */ )
      ),
      (
        "p2sh",
        MergedScript(transaction=Transaction(version=1, inputs=List(NormalTransactionInput(outputTransactionHash=Hash(bytes("790c7f155e430ae71dab1b1078110ef7e11d7f2e49f160857fd3adaa8aba9476")), outputIndex=0L, unlockingScript=UnlockingScript(bytes("0047304402207dc87ea88c8a10bde69dbc91f80c827c7b8a3d18122409e358f1e51b54322e9a022042fe00abb6466dc5e4dbd7f982d2ad9f904e6dc69c6f7eb947223e936a00473e01483045022100f2e9163f61e50cf5984b94f3a388b2c13cf33e442bdf217a944a1b482319af1c022060c48f1e696ad084da336c20618dcc12c1cf14cdf0ef3bd1b0c86e026afe78da014c69522102a2a60f3f6ec13028e58e8fb9ccc53aab9391ea542f35a38b5560138b14bd20a62102b6dad19484515162f51ddc5446bc2a3f622f68dd111282b038b9af107be62ad821037e38809356db2eb6b40b0f14666641f00a041996137f7355a8e1745bac3a4cb453ae")) /* ops:ScriptOpList(operations=Array(Op0(),OpPush(71,ScriptBytes(bytes("304402207dc87ea88c8a10bde69dbc91f80c827c7b8a3d18122409e358f1e51b54322e9a022042fe00abb6466dc5e4dbd7f982d2ad9f904e6dc69c6f7eb947223e936a00473e01"))),OpPush(72,ScriptBytes(bytes("3045022100f2e9163f61e50cf5984b94f3a388b2c13cf33e442bdf217a944a1b482319af1c022060c48f1e696ad084da336c20618dcc12c1cf14cdf0ef3bd1b0c86e026afe78da01"))),OpPushData(1,ScriptBytes(bytes("522102a2a60f3f6ec13028e58e8fb9ccc53aab9391ea542f35a38b5560138b14bd20a62102b6dad19484515162f51ddc5446bc2a3f622f68dd111282b038b9af107be62ad821037e38809356db2eb6b40b0f14666641f00a041996137f7355a8e1745bac3a4cb453ae"))))), hashType:None */, sequenceNumber=4294967295L)), outputs=List(TransactionOutput(value=16337356L, lockingScript=LockingScript(bytes("a91491463c750cff258f7ef5b0aa3dcaea3519ca48ae87")) /* ops:ScriptOpList(operations=Array(OpHash160(),OpPush(20,ScriptBytes(bytes("91463c750cff258f7ef5b0aa3dcaea3519ca48ae"))),OpEqual())) */ ),TransactionOutput(value=880757L, lockingScript=LockingScript(bytes("76a9140e4274d14491be5d5b0ab094b20c52d8abcd795f88ac")) /* ops:ScriptOpList(operations=Array(OpDup(),OpHash160(),OpPush(20,ScriptBytes(bytes("0e4274d14491be5d5b0ab094b20c52d8abcd795f"))),OpEqualVerify(),OpCheckSig(Script(bytes("76a9140e4274d14491be5d5b0ab094b20c52d8abcd795f88ac"))))) */ )), lockTime=0L /* hash:bytes("731036f73eaf04156f520029791866bce88d1ee64b870a0d5a49885313208cb5") */), inputIndex=0, unlockingScript=UnlockingScript(bytes("0047304402207dc87ea88c8a10bde69dbc91f80c827c7b8a3d18122409e358f1e51b54322e9a022042fe00abb6466dc5e4dbd7f982d2ad9f904e6dc69c6f7eb947223e936a00473e01483045022100f2e9163f61e50cf5984b94f3a388b2c13cf33e442bdf217a944a1b482319af1c022060c48f1e696ad084da336c20618dcc12c1cf14cdf0ef3bd1b0c86e026afe78da014c69522102a2a60f3f6ec13028e58e8fb9ccc53aab9391ea542f35a38b5560138b14bd20a62102b6dad19484515162f51ddc5446bc2a3f622f68dd111282b038b9af107be62ad821037e38809356db2eb6b40b0f14666641f00a041996137f7355a8e1745bac3a4cb453ae")) /* ops:ScriptOpList(operations=Array(Op0(),OpPush(71,ScriptBytes(bytes("304402207dc87ea88c8a10bde69dbc91f80c827c7b8a3d18122409e358f1e51b54322e9a022042fe00abb6466dc5e4dbd7f982d2ad9f904e6dc69c6f7eb947223e936a00473e01"))),OpPush(72,ScriptBytes(bytes("3045022100f2e9163f61e50cf5984b94f3a388b2c13cf33e442bdf217a944a1b482319af1c022060c48f1e696ad084da336c20618dcc12c1cf14cdf0ef3bd1b0c86e026afe78da01"))),OpPushData(1,ScriptBytes(bytes("522102a2a60f3f6ec13028e58e8fb9ccc53aab9391ea542f35a38b5560138b14bd20a62102b6dad19484515162f51ddc5446bc2a3f622f68dd111282b038b9af107be62ad821037e38809356db2eb6b40b0f14666641f00a041996137f7355a8e1745bac3a4cb453ae"))))), hashType:None */, lockingScript=LockingScript(bytes("a9140ba8c243f2e21f963cb3c04338b3e0c6918a996c87")) /* ops:ScriptOpList(operations=Array(OpHash160(),OpPush(20,ScriptBytes(bytes("0ba8c243f2e21f963cb3c04338b3e0c6918a996c"))),OpEqual())) */ )
      )
    )

  "scripts" should "be leave true value on top of the stack" in {

    forAll(transactionInputs) { ( subject : String, mergedScript : MergedScript ) =>
//      println("subject: "+subject)
//      println("mergedScript: " + mergedScript )
      verifyTransactionInput(subject, mergedScript.transaction, mergedScript.inputIndex, mergedScript.lockingScript);
    }
  }


}
