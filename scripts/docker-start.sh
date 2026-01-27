

set -e


RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' 


print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}


check_docker() {
    if ! docker info > /dev/null 2>&1; then
        print_error "Docker is not running. Please start Docker first."
        exit 1
    fi
    print_success "Docker is running"
}


check_docker_compose() {
    if ! command -v docker-compose > /dev/null 2>&1; then
        print_error "Docker Compose is not installed."
        exit 1
    fi
    print_success "Docker Compose is available"
}


create_directories() {
    print_status "Creating necessary directories..."
    mkdir -p logs
    mkdir -p database/init-data
    mkdir -p uploads
    print_success "Directories created"
}


start_services() {
    print_status "Building and starting services..."
    
    
    print_status "Pulling latest images..."
    docker-compose pull
    
    
    print_status "Building custom images..."
    docker-compose build --no-cache
    
   
    print_status "Starting services..."
    docker-compose up -d
    
    print_success "Services started successfully"
}


wait_for_services() {
    print_status "Waiting for services to be healthy..."
    
    
    print_status "Waiting for PostgreSQL..."
    timeout 60 bash -c 'until docker-compose exec postgres pg_isready -U helpdesk -d helpdesk; do sleep 2; done'
    
    
    print_status "Waiting for Backend..."
    timeout 120 bash -c 'until curl -f http://localhost:8080/actuator/health; do sleep 5; done'
    
    
    print_status "Waiting for Frontend..."
    timeout 60 bash -c 'until curl -f http://localhost:4200; do sleep 5; done'
    
    print_success "All services are healthy"
}


show_status() {
    print_status "Service Status:"
    docker-compose ps
    
    echo ""
    print_status "Service URLs:"
    echo "Frontend: http://localhost:4200"
    echo "Backend API: http://localhost:8080"
    echo "Backend Health: http://localhost:8080/actuator/health"
    echo "PostgreSQL: localhost:5432"
}


main() {
    echo "========================================"
    echo "  Helpdesk System Docker Start Script"
    echo "========================================"
    echo ""
    
    check_docker
    check_docker_compose
    create_directories
    start_services
    wait_for_services
    show_status
    
    echo ""
    print_success "Helpdesk system is now running!"
    print_status "Use 'docker-compose logs -f' to view logs"
    print_status "Use 'docker-compose down' to stop services"
}


trap 'print_warning "Script interrupted. Cleaning up..."; docker-compose down; exit 1' INT


main "$@"
