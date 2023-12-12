#!/bin/bash

# Step 1: Build the images
echo "Building Docker images..."
docker-compose -f docker-compose.yml build
# Step 2: Run the services
echo "Starting Docker services..."
docker-compose -f docker-compose.yml up -d

echo "Done."