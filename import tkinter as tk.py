import tkinter as tk
from tkinter import messagebox

# -------------------- BACKEND CLASSES --------------------

class ShipmentNode:
    def __init__(self, location, timestamp):
        self.location = location
        self.timestamp = timestamp
        self.next = None
        self.prev = None

class ShipmentHistory:
    def __init__(self):
        self.head = None
        self.tail = None

    def add_history(self, location, timestamp):
        new_node = ShipmentNode(location, timestamp)
        if not self.head:
            self.head = self.tail = new_node
        else:
            self.tail.next = new_node
            new_node.prev = self.tail
            self.tail = new_node

    def get_history(self):
        history = []
        temp = self.head
        while temp:
            history.append(f"{temp.timestamp}: {temp.location}")
            temp = temp.next
        return history

class Package:
    def __init__(self, package_id, priority):
        self.package_id = package_id
        self.priority = priority
        self.history = ShipmentHistory()

    def add_tracking(self, location, timestamp):
        self.history.add_history(location, timestamp)

    def get_tracking_info(self):
        return self.history.get_history()

class PriorityQueueNode:
    def __init__(self, package):
        self.package = package
        self.next = None
        self.prev = None

class PriorityQueue:
    def __init__(self):
        self.head = None

    def enqueue(self, package):
        new_node = PriorityQueueNode(package)
        if not self.head or package.priority > self.head.package.priority:
            new_node.next = self.head
            if self.head:
                self.head.prev = new_node
            self.head = new_node
        else:
            temp = self.head
            while temp.next and temp.next.package.priority >= package.priority:
                temp = temp.next
            new_node.next = temp.next
            if temp.next:
                temp.next.prev = new_node
            temp.next = new_node
            new_node.prev = temp

    def dequeue(self):
        if not self.head:
            return "No packages in queue"
        package = self.head.package
        self.head = self.head.next
        if self.head:
            self.head.prev = None
        return package.package_id

    def get_total_packages(self):
        count = 0
        temp = self.head
        while temp:
            count += 1
            temp = temp.next
        return count

class LogisticsSystem:
    def __init__(self):
        self.packages = {}
        self.delivery_queue = PriorityQueue()

    def add_package(self, package_id, priority):
        if package_id in self.packages:
            return "Package already exists"
        package = Package(package_id, priority)
        self.packages[package_id] = package
        self.delivery_queue.enqueue(package)
        return "Package added"

    def update_tracking(self, package_id, location, timestamp):
        if package_id in self.packages:
            self.packages[package_id].add_tracking(location, timestamp)
            return "Tracking updated"
        return "Package not found"

    def get_tracking_info(self, package_id):
        if package_id in self.packages:
            return self.packages[package_id].get_tracking_info()
        return ["Package not found"]

    def get_next_delivery(self):
        return self.delivery_queue.dequeue()

    def get_total_packages_in_queue(self):
        return self.delivery_queue.get_total_packages()

# -------------------- GUI PART --------------------

logistics = LogisticsSystem()

root = tk.Tk()
root.title("Logistics Package Tracking System")
root.geometry("520x600")

# Add Package Section
tk.Label(root, text="Add Package", font=('Arial', 14, 'bold')).pack(pady=5)
add_frame = tk.Frame(root)
add_frame.pack()

tk.Label(add_frame, text="Package ID:").grid(row=0, column=0, padx=5, pady=5)
pkg_id_entry = tk.Entry(add_frame, width=20)
pkg_id_entry.grid(row=0, column=1)

tk.Label(add_frame, text="Priority:").grid(row=0, column=2, padx=5, pady=5)
priority_entry = tk.Entry(add_frame, width=10)
priority_entry.grid(row=0, column=3)

tk.Button(add_frame, text="Add Package", command=lambda: add_package()).grid(row=0, column=4, padx=5)

# Update Tracking Section
tk.Label(root, text="Update Tracking", font=('Arial', 14, 'bold')).pack(pady=5)
update_frame = tk.Frame(root)
update_frame.pack()

tk.Label(update_frame, text="Package ID:").grid(row=0, column=0, padx=5, pady=5)
upd_id_entry = tk.Entry(update_frame, width=15)
upd_id_entry.grid(row=0, column=1)

tk.Label(update_frame, text="Location:").grid(row=0, column=2, padx=5)
location_entry = tk.Entry(update_frame, width=15)
location_entry.grid(row=0, column=3)

tk.Label(update_frame, text="Timestamp:").grid(row=0, column=4, padx=5)
timestamp_entry = tk.Entry(update_frame, width=15)
timestamp_entry.grid(row=0, column=5)

tk.Button(update_frame, text="Update", command=lambda: update_tracking()).grid(row=0, column=6, padx=5)

# Track Package Section
tk.Label(root, text="Track Package", font=('Arial', 14, 'bold')).pack(pady=5)
track_frame = tk.Frame(root)
track_frame.pack()

tk.Label(track_frame, text="Package ID:").grid(row=0, column=0, padx=5, pady=5)
track_id_entry = tk.Entry(track_frame, width=20)
track_id_entry.grid(row=0, column=1)

tk.Button(track_frame, text="Track", command=lambda: track_package()).grid(row=0, column=2, padx=5)

# Get Next Delivery
tk.Button(root, text="Get Next Delivery", command=lambda: get_next_delivery(), width=25).pack(pady=15)

# Get Total Packages
tk.Button(root, text="Total Packages in Queue", command=lambda: show_total_packages(), width=25).pack(pady=5)

# ------------- FUNCTION DEFINITIONS -------------

def add_package():
    pid = pkg_id_entry.get()
    priority = priority_entry.get()
    if pid and priority.isdigit():
        result = logistics.add_package(pid, int(priority))
        messagebox.showinfo("Info", result)
    else:
        messagebox.showwarning("Invalid Input", "Please enter valid Package ID and numeric Priority.")

def update_tracking():
    pid = upd_id_entry.get()
    loc = location_entry.get()
    ts = timestamp_entry.get()
    if pid and loc and ts:
        result = logistics.update_tracking(pid, loc, ts)
        messagebox.showinfo("Update", result)
    else:
        messagebox.showwarning("Missing Fields", "All fields must be filled.")

def track_package():
    pid = track_id_entry.get()
    info = logistics.get_tracking_info(pid)
    messagebox.showinfo("Tracking Info", "\n".join(info))

def get_next_delivery():
    next_pkg = logistics.get_next_delivery()
    messagebox.showinfo("Next Delivery", f"Next package for delivery: {next_pkg}")

def show_total_packages():
    total = logistics.get_total_packages_in_queue()
    messagebox.showinfo("Total Packages", f"Total packages in queue: {total}")

# --------------------
root.mainloop()
