# Smart Inventory Tracker

Smart Inventory Tracker is a full-stack web application that helps warehouse managers monitor stock levels in real time. It features role-based access control, automated low-stock alerts, and interactive dashboard visualizations. The system is built with Spring Boot, React, and MySQL, and deployed on AWS using Docker for scalability.

## Features

### 🔐 Role-Based Authentication
- **Admin**: Full access to manage users, inventory, and system settings
- **Warehouse Manager**: Can view inventory, update stock, and receive alerts
- **Viewer**: Read-only access to inventory status and dashboards
- JWT-based authentication with secure token management

### 📦 Inventory Management
- Add, edit, and delete inventory items with comprehensive details
- Track SKU, quantity, reorder thresholds, supplier information
- Real-time stock level monitoring
- Maintain complete history/log of inventory changes
- Support for categories, locations, and supplier management

### 🚨 Real-time Alerts
- Automatic low-stock detection based on reorder thresholds
- Email notifications to warehouse managers
- Scheduled daily stock checks (configurable cron job)
- Manual alert trigger capability
- Dashboard alerts and notifications

### 📊 Dashboard & Analytics
- Interactive dashboard with stock health visualization
- Color-coded inventory status (green = healthy, red = low stock)
- Category and supplier distribution charts
- Recent activity tracking
- Comprehensive inventory statistics

### 🔌 REST API
- Comprehensive RESTful API for all operations
- Role-based API security
- Pagination and sorting support
- Advanced search and filtering capabilities
- OpenAPI documentation ready

## Tech Stack

### Backend
- **Java 21** - Programming language
- **Spring Boot 3.5.3** - Application framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database abstraction
- **JWT** - Token-based authentication
- **MySQL 8.0** - Primary database
- **H2** - Testing database
- **Gradle** - Build tool

### Infrastructure & Deployment
- **Docker** - Containerization
- **Docker Compose** - Multi-container orchestration
- **AWS EC2** - Application hosting (deployment ready)
- **AWS RDS** - Database hosting (configuration included)

## Quick Start

### Prerequisites
- Java 21 or higher
- Docker and Docker Compose
- Git

### 1. Clone the Repository
```bash
git clone https://github.com/kushyanthpothi/smart-inventary-tracker.git
cd smart-inventary-tracker
```

### 2. Run with Docker Compose (Recommended)
```bash
# Start all services (MySQL + Application)
docker-compose up --build

# The application will be available at http://localhost:8080
```

### 3. Manual Setup (Alternative)

#### Start MySQL Database
```bash
# Using Docker
docker run -d \
  --name mysql-inventory \
  -e MYSQL_ROOT_PASSWORD=password \
  -e MYSQL_DATABASE=smart_inventory_db \
  -p 3306:3306 \
  mysql:8.0

# Or use existing MySQL installation
```

#### Run the Application
```bash
# Build and run
./gradlew bootRun

# Or build JAR and run
./gradlew build
java -jar build/libs/inventary-0.0.1-SNAPSHOT.jar
```

## Default Users

The application comes with pre-configured users for testing:

| Username | Password | Role | Description |
|----------|----------|------|-------------|
| `admin` | `admin123` | Admin | Full system access |
| `manager` | `manager123` | Warehouse Manager | Inventory management |
| `viewer` | `viewer123` | Viewer | Read-only access |

## API Documentation

### Authentication Endpoints
```
POST /api/auth/signin     - User login
POST /api/auth/signup     - User registration
```

### Inventory Management
```
GET    /api/inventory/items              - Get all items (paginated)
GET    /api/inventory/items/{id}         - Get item by ID
GET    /api/inventory/items/sku/{sku}    - Get item by SKU
POST   /api/inventory/items              - Create new item
PUT    /api/inventory/items/{id}         - Update item
PUT    /api/inventory/items/{id}/stock   - Update stock quantity
DELETE /api/inventory/items/{id}         - Delete item (soft delete)
```

### Search & Filter
```
GET /api/inventory/items/search?searchTerm={term}  - Search items
GET /api/inventory/items/filter?category={cat}     - Filter by category
GET /api/inventory/items/low-stock                 - Get low stock items
```

### Dashboard & Analytics
```
GET  /api/dashboard/stats                    - Dashboard statistics
GET  /api/dashboard/low-stock-items          - Low stock items
GET  /api/dashboard/recent-activity          - Recent inventory changes
POST /api/dashboard/check-alerts             - Trigger manual alert check
```

### Metadata
```
GET /api/inventory/metadata/categories  - Get all categories
GET /api/inventory/metadata/suppliers   - Get all suppliers
GET /api/inventory/metadata/locations   - Get all locations
```

## Configuration

### Database Configuration
Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/smart_inventory_db
spring.datasource.username=root
spring.datasource.password=password
```

### Email Configuration
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

### Alert Scheduling
```properties
# Daily at 8 AM
inventory.alert.cron=0 0 8 * * ?
```

### JWT Configuration
```properties
jwt.secret=your-secret-key-here
jwt.expiration=86400000
```

## Testing

### Run All Tests
```bash
./gradlew test
```

### Test Coverage
```bash
./gradlew jacocoTestReport
# Report available at build/reports/jacoco/test/html/index.html
```

## Deployment

### AWS Deployment

#### 1. Prepare AWS Infrastructure
- Launch EC2 instance (t3.medium or larger recommended)
- Setup RDS MySQL instance
- Configure security groups for ports 8080, 3306, 22

#### 2. Deploy Application
```bash
# On EC2 instance
git clone https://github.com/kushyanthpothi/smart-inventary-tracker.git
cd smart-inventary-tracker

# Update application.properties with RDS endpoint
vim src/main/resources/application.properties

# Build and run
./gradlew build
nohup java -jar build/libs/inventary-0.0.1-SNAPSHOT.jar &
```

#### 3. Using Docker on AWS
```bash
# Install Docker on EC2
sudo yum update -y
sudo yum install -y docker
sudo service docker start

# Clone and run
git clone https://github.com/kushyanthpothi/smart-inventary-tracker.git
cd smart-inventary-tracker
docker-compose up -d
```

## Development

### Project Structure
```
src/
├── main/java/com/kushyanth/inventary/
│   ├── config/          - Configuration classes
│   ├── controller/      - REST controllers
│   ├── dto/            - Data transfer objects
│   ├── entity/         - JPA entities
│   ├── repository/     - Data repositories
│   ├── security/       - Security configuration
│   ├── service/        - Business logic
│   └── util/           - Utility classes
├── main/resources/
│   ├── application.properties  - Main configuration
│   └── application-test.properties  - Test configuration
└── test/              - Test classes
```

### Key Components

#### Entities
- `User` - System users with roles
- `Role` - User roles (Admin, Manager, Viewer)
- `InventoryItem` - Product inventory items
- `InventoryChangeLog` - Audit trail for inventory changes

#### Services
- `InventoryService` - Core inventory business logic
- `AlertService` - Stock alert management
- `EmailService` - Email notification handling
- `UserDetailsServiceImpl` - Spring Security user service

#### Security
- JWT-based authentication
- Role-based access control
- CORS configuration for frontend integration

### Adding New Features

#### 1. Add New Entity
```java
@Entity
@Table(name = "your_entity")
public class YourEntity {
    // Define entity fields and relationships
}
```

#### 2. Create Repository
```java
@Repository
public interface YourEntityRepository extends JpaRepository<YourEntity, Long> {
    // Custom query methods
}
```

#### 3. Implement Service
```java
@Service
@Transactional
public class YourEntityService {
    // Business logic
}
```

#### 4. Add Controller
```java
@RestController
@RequestMapping("/api/your-entity")
public class YourEntityController {
    // REST endpoints
}
```

## Monitoring & Maintenance

### Health Check
```bash
# Application health
curl http://localhost:8080/actuator/health

# Database connection
curl http://localhost:8080/api/inventory/items?page=0&size=1
```

### Logs
```bash
# Application logs
tail -f logs/spring.log

# Docker logs
docker-compose logs -f inventory-app
```

### Database Backup
```bash
# MySQL backup
mysqldump -u root -p smart_inventory_db > backup.sql

# Restore
mysql -u root -p smart_inventory_db < backup.sql
```

## Troubleshooting

### Common Issues

#### 1. Database Connection Issues
- Verify MySQL is running
- Check connection parameters
- Ensure database exists

#### 2. Email Notifications Not Working
- Verify SMTP configuration
- Check email credentials
- Ensure less secure app access (Gmail)

#### 3. JWT Token Issues
- Verify JWT secret configuration
- Check token expiration settings
- Ensure proper header format: `Authorization: Bearer <token>`

#### 4. Permission Denied Errors
- Verify user roles are correctly assigned
- Check security annotations on controllers
- Ensure proper JWT token is being sent

## Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For support and questions:
- Create an issue in the GitHub repository
- Contact: [kushyanthpothi@example.com]

## Roadmap

### Upcoming Features
- [ ] Frontend React application
- [ ] Real-time WebSocket notifications
- [ ] Advanced reporting and analytics
- [ ] Barcode scanning integration
- [ ] Mobile application
- [ ] Multi-tenant support
- [ ] Integration with external ERP systems
- [ ] Advanced user management
- [ ] Audit trail enhancements
- [ ] Performance optimizations
