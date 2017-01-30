One campaign structure:

{
  "id": "ADD10", // unique id, it is stored in a database
  "title": "První odečítání", // campaign title. It can be externalized in strings.xml under key "acdm_"+id
  "picture": "bgt_gradient_salmon", // optional drawable name
  "items": [ // array of tests
    {
      "id": "10", // test id unique within a campaign.
      "order": "ASCENDING", // defines if values will be selected randomly (default) or in ASCENDING/DESCENDING order from formula part defined in "sequence"
      "sequence": "FO", // selects formula part to be used as sequence of values
      "definition": // formula definition specifies how formula is generated
      {
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
      "games": ["FC"], // games to be used, FC fast calc (default), PZ puzzle
      "definition":
      {
        "count": 5,
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
