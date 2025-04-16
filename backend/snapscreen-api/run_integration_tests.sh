#!/bin/bash

# Display info about what this script does
echo "Running Integration Tests for Snapscreen API"
echo "============================================"
echo "This script will run only the integration tests that verify connections to external services"
echo "These tests connect to real services like S3, Firebase Auth, and PostgreSQL"
echo

# Run only the integration tests
./mvnw test -Dgroups=integration

# Check test results
if [ $? -eq 0 ]; then
    echo
    echo "✅ All integration tests passed!"
    echo "Your connections to external services are working correctly."
    echo "The API is ready for integration with the front-end."
else
    echo
    echo "❌ Some integration tests failed."
    echo "Please check your connection settings to external services."
    echo "The API may have issues connecting to S3, Firebase Auth, or PostgreSQL."
fi 