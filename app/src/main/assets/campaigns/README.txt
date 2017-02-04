One campaign structure:

{
  "id": "ADD10", // unique id, it is stored in a database
  "title": "První odečítání", // campaign title. It can be externalized in strings.xml under key "acdm_"+id
  "picture": "bgt_gradient_salmon", // optional drawable name
  "items": [ // array of tests
    {
      "id": "10", // test id unique within a campaign.
      "definition": // formula definition specifies how formula is generated
      {
        "count": 9,
        "order": "ASCENDING", // defines if values will be selected randomly (default) or in ASCENDING/DESCENDING order
                              // from formula part defined in "sequence". Number of tests must not be greater
                              // than available values for selected formula part!
        "sequence": "FO", // selects formula part to be used as sequence of values. Ignored if it equals to unknown.
        "operators": [ // definition for formula operator values
          {
            "operator": ["+"], // operators which share this configuration
            "first": "1-9", // first operand values
            "second": "1", // second operand values
            "result": "2-10" // result values
          }
        ]
      }
    },
    {
      "id": "20",
      "definition":
      {
        "count": 5,
        "games": ["FC"], // games to be used, FC fast calc (default), PZ puzzle
        "unknowns": ["RS"], // formula parts that can be set as unknown. FO first operand, SO second operand, OP operator, RS result
        "operators": [
          {
            "operator": ["+"],
            "first": "0-5",
            "second": "0-5",
            "result": "5-10"
          },
          {
            "operator": ["-"],
            "first": "3-5",
            "second": "1,2",
            "result": "1-3"
          }
        ]
      }
    }
  ]
}

Notes:

* FIXED_PAIRS - values from first and second arguments are taken sequentially,
number of tests must be smaller than number of values. Same unknown is used for all formulas.

