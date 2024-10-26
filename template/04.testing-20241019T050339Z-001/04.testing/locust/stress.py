from locust import FastHttpUser, task, between, TaskSet
from locust import events
from locust.runners import MasterRunner, WorkerRunner

class MyTask(TaskSet):
    # 느린거 100개찌를때 빠른거 1번
    @task(100) 
    def get(self):
        self.client.get(f'/article/499')
    @task(1)
    def getAll(self):
        self.client.get(f'/article/all?title=matched')

class LocustUser(FastHttpUser):
    host = "http://localhost:8080"
    tasks = [ MyTask ]
    min_wait = 5000
    max_wait = 15000
