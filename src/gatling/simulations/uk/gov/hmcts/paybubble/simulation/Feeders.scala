package uk.gov.hmcts.paybubble.simulation

object Feeders {


  val DataGenBulkScanFeeder = Iterator.continually(Map("service" -> ({
    "BSDataGen"
  }),
    "SignoutNumber" -> ({
      "260"
    })

  ));

  val OnlinePaymentFeeder = Iterator.continually(Map("service" -> ({
    "Online"
  }),
    "SignoutNumber" -> ({
      "260"
    })

  ));

  val BulkscanFeeder = Iterator.continually(Map("service" -> ({
    "BulkScan"
  }),
    "SignoutNumber" -> ({
      "380"
    })
  ));

  val TelephoneFeeder = Iterator.continually(Map("service" -> ({
    "Telephony"
  }),
    "SignoutNumber" -> ({
      "070"
    })
  ));

  val PBAFeeder = Iterator.continually(Map("service" -> ({
    "PBA"
  }),
    "SignoutNumber" -> ({
      "070"
    })
  ));

  val ViewPaymentsFeeder = Iterator.continually(Map("service" -> ({
    "ViewPayments"
  }),
    "SignoutNumberAdmin" -> ({
      "150"
    }),
    "SignoutNumberGK" -> ({
      "290"
    })

  ));







}



