

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


show_usage() {
    echo "Usage: $0 [SERVICE] [OPTIONS]"
    echo ""
    echo "Services:"
    echo "  postgres     Show PostgreSQL logs"
    echo "  backend      Show Backend logs"
    echo "  frontend     Show Frontend logs"
    echo "  all          Show all logs (default)"
    echo ""
    echo "Options:"
    echo "  -f, --follow     Follow log output (live)"
    echo "  -t, --tail N     Show last N lines (default: 100)"
    echo "  --since TIME     Show logs since TIME (e.g., 1h, 30m, 2023-01-01T00:00:00)"
    echo "  --help, -h       Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0                  Show all logs (last 100 lines)"
    echo "  $0 -f               Follow all logs"
    echo "  $0 backend -f       Follow backend logs"
    echo "  $0 postgres --tail 50  Show last 50 lines of PostgreSQL logs"
    echo "  $0 all --since 1h    Show all logs from last hour"
}


SERVICE="all"
FOLLOW=false
TAIL="100"
SINCE=""

while [[ $# -gt 0 ]]; do
    case $1 in
        postgres|backend|frontend|all)
            SERVICE="$1"
            shift
            ;;
        -f|--follow)
            FOLLOW=true
            shift
            ;;
        -t|--tail)
            TAIL="$2"
            shift 2
            ;;
        --since)
            SINCE="$2"
            shift 2
            ;;
        --help|-h)
            show_usage
            exit 0
            ;;
        *)
            print_error "Unknown option: $1"
            show_usage
            exit 1
            ;;
    esac
done


check_docker() {
    if ! docker info > /dev/null 2>&1; then
        print_error "Docker is not running."
        exit 1
    fi
}


check_services() {
    if ! docker-compose ps -q | grep -q .; then
        print_error "No services are currently running."
        print_status "Start services with: ./scripts/docker-start.sh"
        exit 1
    fi
}


build_logs_command() {
    local cmd="docker-compose logs"
    
    if [ "$FOLLOW" = true ]; then
        cmd="$cmd -f"
    fi
    
    if [ -n "$TAIL" ] && [ "$TAIL" != "100" ]; then
        cmd="$cmd --tail=$TAIL"
    fi
    
    if [ -n "$SINCE" ]; then
        cmd="$cmd --since=$SINCE"
    fi
    
    echo "$cmd"
}


show_service_logs() {
    local service="$1"
    local cmd=$(build_logs_command)
    
    print_status "Showing logs for: $service"
    echo "Command: $cmd $service"
    echo "----------------------------------------"
    
    $cmd "$service"
}


show_all_logs() {
    local cmd=$(build_logs_command)
    
    print_status "Showing logs for all services"
    echo "Command: $cmd"
    echo "========================================"
    
    $cmd
}


main() {
    echo "========================================"
    echo "  Helpdesk System Docker Logs Script"
    echo "========================================"
    echo ""
    
    check_docker
    check_services
    
    case $SERVICE in
        postgres)
            show_service_logs "postgres"
            ;;
        backend)
            show_service_logs "backend"
            ;;
        frontend)
            show_service_logs "frontend"
            ;;
        all)
            show_all_logs
            ;;
    esac
}


trap 'print_warning "Log viewing interrupted. Press Ctrl+C again to exit."; exit 0' INT


main "$@"
