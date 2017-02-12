Notes:

"id" - identifier stored with results in a database. It must be unique between
campaigns / items of certain campaign.

"title" - campaign title. If empty, a key "acdm_"+id from strings.xml will be used.

"picture" - optional drawable name. If empty random picture will be used.

"items" - array of tests

"definition" - formula definition specifies how formula is generated

"count" - number of formulas generated in this test

"unknowns" - array of formula parts that can be set as unknown. FO first operand,
SO second operand, OP operator, RS result.

"games" - array of games to be used, FC stands for fast calc (default), PZ for puzzle

"order" - can have following values: RANDOM (default), ASCENDING, DESCENDING, FIXED_PAIRS

* FIXED_PAIRS - values from first and second operands are taken sequentially, range is not allowed.
Ascending order is not enforced for the values. Number of tests must not be greater than number
of values. All formulas will have same unknown.

* ASCENDING, DESCENDING - values from formula part defined in "sequence" are taken sequentially in
requested order. Number of tests must not be greater than number of values.

"sequence" - selects formula part to be used as sequence of values. Ignored if set
to the same value as unknown.

"operator" - array of operators sharing this configuration. It can be +, -, *, /.

"first", "second", "result" - argument values are defined by list of values or ranges
in ascending order. E.g. 1-3,5,6,7-10


One campaign structure:

{
  "id": "ADD10",
  "title": "First summation",
  "picture": "bgt_gradient_salmon",
  "items": [
    {
      "id": "10",
      "definition":
      {
        "count": 9,
        "order": "ASCENDING",
        "sequence": "FO",
        "operators": [
          {
            "operator": ["+"],
            "first": "1-9",
            "second": "1",
            "result": "2-10"
          }
        ]
      }
    },
    {
      "id": "20",
      "definition":
      {
        "count": 5,
        "games": ["FC"],
        "unknowns": ["RS"],
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
