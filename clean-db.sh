#!/bin/bash

echo "Stopping and removing containers..."
docker-compose down

echo "Removing PostgreSQL volume..."
docker volume rm recicar_postgres_data

echo "Starting containers fresh..."
docker-compose up -d

echo "Waiting for PostgreSQL to be ready..."
sleep 10

echo "Database cleaned and ready!"
echo "You can now run your application."
