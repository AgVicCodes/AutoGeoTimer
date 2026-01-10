import random
from time import time, sleep
from geopy.distance import geodesic
from geopy.geocoders import Nominatim
from datetime import datetime, timedelta


class Locator:
    """Initialises the locator class converting address to longitude and latitude"""
    def __init__(self, address: str):
        self.geolocator = Nominatim(user_agent = "work_time_tracker")
        self.address = address
        self.location = self.geolocator.geocode(address, timeout = 10)

    def lookup_address(self) -> tuple[float, float]:
        if not self.address:
            raise ValueError(f"Could not geocode: {self.address}")
        return (self.location.latitude, self.location.longitude)

    def is_inside_geofence(self, 
                           point: tuple[float, float], 
                           center: tuple[float, float], 
                           radius_m: float = 100) -> bool:
        return geodesic(point, center).meters <= radius_m

    def record_event(self, user_id: str, event: str, timestamp: datetime):
        print(f"{timestamp.isoformat()} - {user_id} {event}")

    def compute_time_worked(
        self,
        steps: int = 20,
        delta_min: float = 0.0001,
        delta_max: float = 0.0005,
        interval_s: float = 3.0
    ) -> timedelta:
        # Center declaration
        center = self.lookup_address()
        path = [(center[0], center[1])]

        # 
        for _ in range(steps):
            d = random.uniform(delta_min, delta_max)
            d = random.choice([d, d * (-1)])
            lat, lon = path[-1]
            path.append((lat + d, lon + d))

        # Record check-in time
        start = datetime.now()
        print(f"▶ Checked in at {start}")

        # Traverse path and simulate timing
        end = start
        for idx, point in enumerate(path, 1):
            if self.is_inside_geofence(point, center):
                sleep(interval_s)            # simulate delay per reading
                end += timedelta(seconds = interval_s)
            else:
                break

        # Record check-out and compute duration
        print(f"⏹ Checked out at {end}")
        print(f"Total time inside geofence: {end - start}")
        return end - start

center = Locator("LU3 3QN")
point = Locator("LU3 3QL")

print(point.address)
print(point.location)
print(point.lookup_address())
print(center.lookup_address())
print(point.is_inside_geofence(point = point.lookup_address(), center = center.lookup_address()))


print(center.compute_time_worked())
# print(center.compute_time_worked())

# class Locator(Encoder):
#     def __init__(self, address):
#         super().__init__(address)