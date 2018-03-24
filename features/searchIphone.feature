Feature: Search Iphone in Google

Scenario: Search iPhone in google and navigate to Amazon
Given I navigate to google website
When I search iphone
Then I see list of suggestions
When I search for amazon in suggestions
And I click on it
Then I check if the product is iPhone smartPhone
And I identify highest memory
Then I assert the price of the product
And I print product information
Then I close the browser