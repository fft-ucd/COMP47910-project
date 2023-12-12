#!/bin/bash

# Step 2: Run the services
echo "Stopping Docker services..."
docker-compose -f docker-compose.yml down

echo "Done."