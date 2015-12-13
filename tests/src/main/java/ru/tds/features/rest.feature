Feature: Smoke tests
  Verify that response has code 200 for some requests

  @getToken
  Scenario: Verify that response has code 200 and valid token
    When send POST request for getting token
#    Then response has code 200
    And response has valid token

