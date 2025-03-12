import java.util.Date;
  
// Base class for all packages (Parent class)
class BasePackage {
    String trackingID;
    String sender;
    String recipient;
    int priority; // Lower number = higher priority
    ShipmentHistory shipmentHistory;

    public BasePackage(String trackingID, String sender, String recipient, int priority) {
        this.trackingID = trackingID;
        this.sender = sender;
        this.recipient = recipient;
        this.priority = priority;
        this.shipmentHistory = new ShipmentHistory();
    }

    public void updateStatus(String status, String location) {
        shipmentHistory.addUpdate(status, location);
    }

    // Overridden by subclasses
    public void printTrackingInfo() {
        System.out.println("Tracking ID: " + trackingID);
        System.out.println("Sender: " + sender);
        System.out.println("Recipient: " + recipient);
        System.out.println("Priority: " + priority);
        System.out.println("Shipment History:");
        shipmentHistory.printHistory();
    }
}

// Child class for standard packages
class StandardPackage extends BasePackage {
    public StandardPackage(String trackingID, String sender, String recipient, int priority) {
        super(trackingID, sender, recipient, priority);
    }

    @Override
    public void printTrackingInfo() {
        System.out.println("=== Standard Package Tracking ===");
        super.printTrackingInfo();
    }
}

// Child class for fragile packages
class FragilePackage extends BasePackage {
    public FragilePackage(String trackingID, String sender, String recipient, int priority) {
        super(trackingID, sender, recipient, priority);
    }

    @Override
    public void printTrackingInfo() {
        System.out.println("=== Fragile Package - Handle with Care ===");
        super.printTrackingInfo();
    }
}

// Child class for perishable packages
class PerishablePackage extends BasePackage {
    public PerishablePackage(String trackingID, String sender, String recipient, int priority) {
        super(trackingID, sender, recipient, priority);
    }

    @Override
    public void printTrackingInfo() {
        System.out.println("=== Perishable Package - Keep Refrigerated ===");
        super.printTrackingInfo();
    }
}

// Node class for shipment history (Linked List)
class ShipmentNode {
    String status;
    String location;
    String timestamp;
    ShipmentNode next;

    public ShipmentNode(String status, String location) {
        this.status = status;
        this.location = location;
        this.timestamp = new Date().toString();
        this.next = null;
    }
}

// Linked List for shipment history
class ShipmentHistory {
    private ShipmentNode head;

    public void addUpdate(String status, String location) {
        ShipmentNode newNode = new ShipmentNode(status, location);
        if (head == null) {
            head = newNode;
        } else {
            ShipmentNode current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
    }

    public void printHistory() {
        ShipmentNode current = head;
        while (current != null) {
            System.out.println("[" + current.timestamp + "] " + current.status + " - " + current.location);
            current = current.next;
        }
    }
}

// Min-Heap Priority Queue for package deliveries
class DeliveryQueue {
    private BasePackage[] heap;
    private int size;
    private int capacity;

    public DeliveryQueue(int capacity) {
        this.capacity = capacity;
        this.heap = new BasePackage[capacity];
        this.size = 0;
    }

    private void swap(int i, int j) {
        BasePackage temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    private void heapifyUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (heap[index].priority < heap[parent].priority) {
                swap(index, parent);
                index = parent;
            } else {
                break;
            }
        }
    }

    private void heapifyDown(int index) {
        while (2 * index + 1 < size) {
            int leftChild = 2 * index + 1;
            int rightChild = 2 * index + 2;
            int smallest = leftChild;

            if (rightChild < size && heap[rightChild].priority < heap[leftChild].priority) {
                smallest = rightChild;
            }

            if (heap[index].priority > heap[smallest].priority) {
                swap(index, smallest);
                index = smallest;
            } else {
                break;
            }
        }
    }

    public void addPackage(BasePackage pkg) {
        if (size == capacity) {
            System.out.println("Queue is full! Cannot add more packages.");
            return;
        }
        heap[size] = pkg;
        heapifyUp(size);
        size++;
    }

    public BasePackage deliverPackage() {
        if (size == 0) {
            System.out.println("No packages in queue!");
            return null;
        }
        BasePackage delivered = heap[0];
        heap[0] = heap[size - 1];
        size--;
        heapifyDown(0);
        return delivered;
    }

    public void printQueue() {
        if (size == 0) {
            System.out.println("No packages in queue!");
            return;
        }
        for (int i = 0; i < size; i++) {
            System.out.println("Tracking ID: " + heap[i].trackingID + ", Priority: " + heap[i].priority);
        }
    }
}

// Main Class
public class LogisticsTrackingSystem {
    public static void main(String[] args) {
        DeliveryQueue deliveryQueue = new DeliveryQueue(10);

        // Create different types of packages
        BasePackage package1 = new StandardPackage("PKG001", "Alice", "Bob", 1);
        BasePackage package2 = new FragilePackage("PKG002", "Charlie", "David", 0);
        BasePackage package3 = new PerishablePackage("PKG003", "Eve", "Frank", 2);

        // Add shipment updates
        package1.updateStatus("Dispatched", "New York");
        package1.updateStatus("In Transit", "Chicago");
        package1.updateStatus("Delivered", "San Francisco");

        package2.updateStatus("Dispatched", "Los Angeles");
        package2.updateStatus("Out for Delivery", "San Francisco");

        package3.updateStatus("Dispatched", "Houston");

        // Add packages to priority queue
        deliveryQueue.addPackage(package1);
        deliveryQueue.addPackage(package2);
        deliveryQueue.addPackage(package3);

        // View queue before delivery
        System.out.println("\nDelivery Queue Before:");
        deliveryQueue.printQueue();

        // Deliver the highest priority package
        BasePackage delivered = deliveryQueue.deliverPackage();
        if (delivered != null) {
            System.out.println("\nDelivered Package:");
            delivered.printTrackingInfo();
        }

        // View queue after delivery
        System.out.println("\nDelivery Queue After:");
        deliveryQueue.printQueue();
    }
}
