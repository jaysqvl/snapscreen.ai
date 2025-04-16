#!/bin/bash

# Display info about what this script does
echo "Running API tests for Snapscreen API"
echo "=================================="
echo "This script will run all API tests using Maven"
echo

# Run the tests
./mvnw test

# Check test results
if [ $? -eq 0 ]; then
    echo
    echo "✅ All tests passed!"
    echo "API endpoints are ready for integration with the front-end."
else
    echo
    echo "❌ Some tests failed."
    echo "Please fix the issues before integrating with the front-end."
fi 