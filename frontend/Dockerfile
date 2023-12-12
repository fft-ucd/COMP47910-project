# Step 1: Build stage
FROM node:lts-alpine as build-stage

# Set the working directory in the container to /app
WORKDIR /app

# Copy package.json and package-lock.json to the workdir
COPY package*.json ./

# Install any needed packages specified in package.json
RUN npm install

# Copy the current directory contents into the container at /app
COPY . .

# Make the build
RUN npm run build

# Step 2: Production stage
FROM nginx:stable-alpine as production-stage

# Change the ownership of the /var/cache/nginx and /var/run directories to root
USER root
RUN chown -R root:root /var/cache/nginx /var/run

# Copy the build files from the build stage to the /usr/share/nginx/html directory
COPY --from=build-stage /app/dist /usr/share/nginx/html

# Copy the nginx configuration file
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Start Nginx server as root user
CMD ["nginx", "-g", "daemon off;"]
