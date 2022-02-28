import socket

TCP_IP = "localhost"
TCP_PORT = 6666
BUFFER_SIZE = 1024

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((TCP_IP, TCP_PORT))
s.listen(1)

print("Waiting to accept")
conn, addr = s.accept()
print("Connection address:", addr)
while True:
    data = conn.recv(BUFFER_SIZE)
    if not data:
        break
    print("Received data:", data)
    conn.send(b"hello client\n")
conn.close()

