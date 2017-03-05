Notes:

"id" - identifier stored with results in a database. It must be unique between
campaigns / items of certain campaign.

"title" - campaign title. If empty, a key "acdm_"+id from strings.xml will be used.

"picture" - optional drawable name. If empty random picture will be used.

"items" - array of tests

"definition" - formula definition specifies how formula is generated

"count" - number of formulas generated in this test

"unknowns" - array of formula parts that can be set as unknown. FO first operand,
SO second operand, OP first operator, OP2 second operator, TO third operand and RS result.

"games" - array of games to be used, FC stands for fast calc (default), PZ for puzzle

"order" - can have following values: RANDOM (default), ASCENDING, DESCENDING

* ASCENDING, DESCENDING - values are taken sequentially in requested order.
Number of tests must not be greater than number of values.
If multiple values use defined order than all values must have same size.

"operator" - array of operators sharing this configuration. It can be +, -, *, /.

"first", "second", "third", "result" - argument values are defined by list of values or ranges
in ascending order. E.g. 1-3,5,6,7-10.

If expressions are defined in simple form then count of formulas must be same as number of expressions.
They will be used sequentially.

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
        "expressions": [
        {
          "first": {"values": "4-9", "order": "ASCENDING"},
          "operator": "+",
          "second": "1-6",
          "operator2": "+",
          "third": "4-7",
          "result": "15-85"
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
        "expressions": [
          {
            "first": "0-5",
            "operator": ["+"],
            "second": "0-5",
            "result": "5-10"
          },
          {
            "first": "3-5",
            "operator": ["-"],
            "second": "1,2",
            "result": "1-3"
          }
        ]
      }
    },
    {
      "id": "30",
      "definition":
      {
        "count": 4,
        "expressions": [
            "1+1", "2-1", "2-2", "2-0"
        ]
      }
    }
  ]
}
