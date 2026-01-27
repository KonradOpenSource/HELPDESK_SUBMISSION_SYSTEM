

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
        print_error "Docker is not running."
        exit 1
    fi
}


stop_services() {
    print_status "Stopping Helpdesk services..."
    
    if docker-compose ps -q | grep -q .; then
        docker-compose down
        print_success "Services stopped successfully"
    else
        print_warning "No services are currently running"
    fi
}


remove_volumes() {
    if [ "$1" = "--remove-volumes" ]; then
        print_warning "This will remove all Docker volumes. Are you sure? (y/N)"
        read -r response
        if [[ "$response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
            print_status "Removing Docker volumes..."
            docker-compose down -v
            print_success "Docker volumes removed"
        else
            print_status "Volume removal cancelled"
        fi
    fi
}


remove_images() {
    if [ "$1" = "--remove-images" ]; then
        print_warning "This will remove all Docker images. Are you sure? (y/N)"
        read -r response
        if [[ "$response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
            print_status "Removing Docker images..."
            docker-compose down --rmi all
            print_success "Docker images removed"
        else
            print_status "Image removal cancelled"
        fi
    fi
}


cleanup_docker() {
    print_status "Cleaning up unused Docker resources..."
    docker system prune -f
    print_success "Docker cleanup completed"
}


show_status() {
    print_status "Final Docker status:"
    docker-compose ps 2>/dev/null || echo "No containers running"
    
    echo ""
    print_status "Docker disk usage:"
    docker system df
}


main() {
    echo "========================================"
    echo "  Helpdesk System Docker Stop Script"
    echo "========================================"
    echo ""
    
    check_docker
    stop_services
    remove_volumes "$1"
    remove_images "$2"
    cleanup_docker
    show_status
    
    echo ""
    print_success "Helpdesk system has been stopped!"
}


if [[ "$1" == "--help" || "$1" == "-h" ]]; then
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  --remove-volumes    Remove Docker volumes (with confirmation)"
    echo "  --remove-images     Remove Docker images (with confirmation)"
    echo "  --help, -h          Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0                  Stop services only"
    echo "  $0 --remove-volumes Stop services and remove volumes"
    echo "  $0 --remove-volumes --remove-images Stop services and remove everything"
    exit 0
fi


main "$@"
