const express = require('express');
const http = require('http');
const { Server } = require('socket.io');
const cors = require('cors');

const app = express();
app.use(cors());
app.use(express.json());

const server = http.createServer(app);
const io = new Server(server, {
  cors: {
    origin: '*',
    methods: ['GET', 'POST']
  }
});

// Rooms state store: roomId -> { hostId, currentMovie, playback: { isPlaying, positionMs, timestamp } }
const rooms = new Map();

// Health check endpoint for Render
app.get('/', (req, res) => {
  res.json({
    status: 'online',
    service: 'OurScreen WatchParty & Screen Share Signaling Server',
    activeRooms: rooms.size,
    timestamp: new Date().toISOString()
  });
});

app.get('/health', (req, res) => {
  res.status(200).send('OK');
});

io.on('connection', (socket) => {
  console.log(`[Connect] Socket ID: ${socket.id}`);

  // Join Room
  socket.on('join_room', ({ roomId, username }) => {
    socket.join(roomId);
    socket.data.roomId = roomId;
    socket.data.username = username || `User_${socket.id.substring(0, 4)}`;

    if (!rooms.has(roomId)) {
      rooms.set(roomId, {
        hostId: socket.id,
        currentMovie: null,
        playback: { isPlaying: false, positionMs: 0, timestamp: Date.now() },
        members: new Set()
      });
    }

    const room = rooms.get(roomId);
    room.members.add(socket.id);

    console.log(`[Join] ${socket.data.username} joined room ${roomId}. Total members: ${room.members.size}`);

    // Notify room of updated participant list
    io.to(roomId).emit('room_update', {
      roomId,
      memberCount: room.members.size,
      hostId: room.hostId,
      playback: room.playback,
      currentMovie: room.currentMovie
    });

    // Send welcome / state to user
    socket.emit('joined_successfully', {
      roomId,
      isHost: room.hostId === socket.id,
      playback: room.playback
    });
  });

  // Playback Synchronize (Play / Pause / Seek)
  socket.on('sync_playback', ({ roomId, isPlaying, positionMs }) => {
    const room = rooms.get(roomId);
    if (room) {
      room.playback = {
        isPlaying,
        positionMs,
        timestamp: Date.now()
      };
      // Broadcast playback command to everyone else in the room
      socket.to(roomId).emit('playback_changed', room.playback);
      console.log(`[Sync] Room ${roomId} playback changed: playing=${isPlaying}, pos=${positionMs}`);
    }
  });

  // Screen Share WebRTC Signaling
  socket.on('screenshare_signal', ({ roomId, data }) => {
    socket.to(roomId).emit('screenshare_signal', {
      from: socket.id,
      data
    });
  });

  // Screen Share Status Toggle
  socket.on('screenshare_status', ({ roomId, isSharing, sourceTitle }) => {
    socket.to(roomId).emit('screenshare_status_update', {
      from: socket.id,
      isSharing,
      sourceTitle
    });
  });

  // Chat message
  socket.on('send_chat', ({ roomId, message }) => {
    io.to(roomId).emit('new_chat', {
      id: `${Date.now()}_${socket.id}`,
      sender: socket.data.username || 'Anonymous',
      message,
      timestamp: Date.now()
    });
  });

  // Disconnect
  socket.on('disconnect', () => {
    const roomId = socket.data.roomId;
    if (roomId && rooms.has(roomId)) {
      const room = rooms.get(roomId);
      room.members.delete(socket.id);

      if (room.members.size === 0) {
        rooms.delete(roomId);
        console.log(`[Room Closed] Room ${roomId} cleaned up.`);
      } else {
        if (room.hostId === socket.id) {
          // Reassign host to next available member
          room.hostId = Array.from(room.members)[0];
        }
        io.to(roomId).emit('room_update', {
          roomId,
          memberCount: room.members.size,
          hostId: room.hostId,
          playback: room.playback
        });
      }
    }
    console.log(`[Disconnect] Socket ID: ${socket.id}`);
  });
});

const PORT = process.env.PORT || 10000;
server.listen(PORT, () => {
  console.log(`[Server Ready] WatchParty server running on port ${PORT}`);
});
