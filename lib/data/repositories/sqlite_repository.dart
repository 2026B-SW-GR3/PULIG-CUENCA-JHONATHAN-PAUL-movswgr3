import 'package:sqflite/sqflite.dart';
import 'package:path/path.dart';
import '../../domain/models/task.dart';
import '../../domain/repositories/data_repository.dart';
import '../../core/logger.dart';

class SqliteRepository implements DataRepository {
  Database? _database;

  Future<Database> get database async {
    if (_database != null) return _database!;
    _database = await _initDB();
    return _database!;
  }

  Future<Database> _initDB() async {
    final dbPath = await getDatabasesPath();
    final path = join(dbPath, 'tasks.db');

    return await openDatabase(
      path,
      version: 1,
      onCreate: (db, version) async {
        await db.execute('''
          CREATE TABLE tasks(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            title TEXT,
            description TEXT
          )
        ''');
      },
    );
  }

  @override
  String get sourceName => "SQLite (Relacional)";

  @override
  Future<List<Task>> getAllTasks() async {
    final db = await database;
    final List<Map<String, dynamic>> maps = await db.query('tasks');
    Logger.debug('Reading from SQLite: ${maps.length} tasks found');
    return List.generate(maps.length, (i) => Task.fromMap(maps[i]));
  }

  @override
  Future<void> addTask(Task task) async {
    final db = await database;
    await db.insert('tasks', task.toMap(), conflictAlgorithm: ConflictAlgorithm.replace);
    Logger.info('SQLite: Task inserted - ${task.title}');
  }

  @override
  Future<void> updateTask(Task task) async {
    final db = await database;
    await db.update('tasks', task.toMap(), where: 'id = ?', whereArgs: [task.id]);
    Logger.info('SQLite: Task updated - ${task.title}');
  }

  @override
  Future<void> deleteTask(int id) async {
    final db = await database;
    await db.delete('tasks', where: 'id = ?', whereArgs: [id]);
    Logger.info('SQLite: Task deleted - ID $id');
  }
}
