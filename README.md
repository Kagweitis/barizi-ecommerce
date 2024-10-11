# Barizi E-commerce

## Project Description

Barizi E-commerce is a Spring Boot backend service designed for an e-commerce platform. It allows management of products, users, and orders, implementing essential functionalities such as product management, user registration and authentication, order placement, and tracking. The service is built to demonstrate proficiency in designing a service layer, creating RESTful APIs, handling data relationships, and writing integration tests.

## Getting Started

### 1. Clone the Repository

To get a local copy of the project, clone it using the following command:

```bash
git clone https://github.com/Kagweitis/barizi-ecommerce.githttps://github.com/Kagweitis/barizi-ecommerce.git
```

### 2. Database Setup

1. Create a database called **barizi_ecommerce**
2. Open the `src/main/resources/application.properties` file in your project.
3. Change the database credentials to match your local MySQL setup. Update the following properties:

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/barizi_ecommerce
   spring.datasource.username=your_username
   spring.datasource.password=your_password
    ```

### 3. Authentication Endpoints

The application includes two authentication endpoints for user registration and login, supporting two roles: **CUSTOMER** and **ADMIN**.

- **Register Endpoint**
  - **URL:** `auth/api/v1/register`
  - **Method:** `POST`
    - **Request Body:**
      ```json
      {
        "firstName": "string",
        "lastName": "string",
        "userName": "string",
        "phoneNumber": "string",
        "email": "string",
        "password": "string",
        "role": "CUSTOMER" //or ADMIN
      }
    ```
  - **Description:** This endpoint allows new users to register. The role should be specified as either **CUSTOMER** or **ADMIN**.

- **Login Endpoint**
  - **URL:** `/api/auth/login`
  - **Method:** `POST`
  - **Request Body:**
    ```json
    {
      "username": "your_username",
      "password": "your_password"
    }
    ```
  - **Description:** This endpoint allows users to log in and receive a JWT token, which must be used for accessing protected resources.

### 4. User Roles and Permissions

The application defines two user roles: **Customer** and **Admin**, each with specific permissions.

  #### Customer Role
  Users with the **Customer** role have the following permissions:
  - **Search for Products:** Customers can search for products by name or category.
  - **Get Products:** Customers can view the list of available products.
  - **Manage Orders:**
    - **Place Orders:** Customers can create new orders. Including **Payment Status** `(PAID, UNPAID, FAILED)` and **Order Status** `(DELIVERED, SHIPPED, PENDING)`
    - **Update Orders:** Customers can modify their existing orders.
    - **Delete Orders:** Customers can delete their orders.
    - **Get Their Orders:** Customers can view the details of their past orders.
  
  #### Admin Role
  Users with the **Admin** role have broader permissions, including:
  - **Manage Products:**
    - **View Products:** Admins can access the list of all products.
    - **Add Products:** Admins can create new products.
    - **Update Products:** Admins can modify existing products.
    - **Delete Products:** Admins can remove products from the inventory.
  - **Access All Protected Resources in Orders:** Admins can view and manage all orders placed by customers.
  
  These role-based permissions ensure a secure and structured approach to managing access to different functionalities within the application.

### 5. Categories
For a more seamless experience, I have created a service to autocreate and save categories to facilitate easy creation of products. Please access the categories in the database after application start and use any of the cataegory IDs available to categorize new products when sending the request to add or update products.
### 6. Testing

To ensure the functionality of the application, integration tests have been implemented for key components, including:

- **Product Management:** Tests cover CRUD operations for products, ensuring proper data validation and handling of edge cases.
- **User Authentication:** Tests verify the registration and login processes, ensuring roles are correctly assigned and JWT tokens are generated.
- **Order Management:** Tests validate the placement and tracking of orders, ensuring that orders are correctly associated with users.
- **Category Management:** Tests validate the addition of new categories.

To run the tests, use the following command:

```bash
mvn test
```

### 7. Running The Application

To run the application, you can use an IDE of choice and run the main class or open terminal/cmd and run
```bash
mvn clean install -DskipTests
```
to create a jar file. Open cmd/terminal and navigate to the `/target` folder and run 
```bash
java -jar barizi-0.0.1-SNAPSHOT
```
### 8. Deployment

This project can be deployed using a Jenkins pipeline that builds the application, creates a Docker image, pushes it to a Docker registry, and deploys it as a container. Below are the steps to set up and run the deployment pipeline.

#### Prerequisites
- Ensure you have Jenkins installed and running.
- Docker should be installed on the Jenkins server.
- Create a Docker Hub account and add your credentials to Jenkins (under "Credentials") with the ID `dockerhub_credentials`.

#### Jenkins Pipeline Configuration
1. **Create a New Pipeline Job in Jenkins:**
  - Open Jenkins and click on "New Item".
  - Select "Pipeline" and give it a name (e.g., `barizi-ecommerce-deployment`).
  - Click "OK".
2. **Configure the Pipeline:**
  - In the pipeline configuration, scroll down to the "Pipeline" section.
  - Choose "Get from scm" to use the Jenkinsfile at the root of the project in github.
  - When the job is run Jenkins will build the application (jar file) and create an image. 
  - The pipeline will, further, push the image to Dockerhub using the credentials set and subsequently build a docker container using the image built thereby deploying the application.
  - Access the application via http://{{baseUrl}}:8081

### 9. Documentation

### Postman Setup

To test the API endpoints using Postman, follow these steps:

1. **Create Environment Variables:**
  - Open Postman and click on the "Environments" tab.
  - Click on "Add" to create a new environment.
  - Name your environment (e.g., `Barizi E-commerce`).
  - Add the following environment variables:
    - `token`: This will hold the JWT token for authentication.
    - `baseUrl`: Set the value to `http://localhost:8081`.

2. **Testing the API:**
  - Once your application is up and running, you can authenticate by sending a POST request to the `register` or `login` endpoints.
  - Save the JWT token returned from the response and set it as the value for the `token` environment variable in Postman.
  - Use the `{{baseUrl}}` variable in your requests to access the API endpoints.

### Swagger Docs

After starting the application, you can access the **Swagger documentation** at the following URL:

[http://localhost:8081/swagger-ui/index.html#/](http://localhost:8081/swagger-ui/index.html#/)

This documentation provides an interactive interface to explore and test the API endpoints available in the `barizi-ecommerce` application.

