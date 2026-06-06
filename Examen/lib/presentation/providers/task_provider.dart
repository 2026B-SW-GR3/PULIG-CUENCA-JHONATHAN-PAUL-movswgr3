import 'package:flutter/material.dart';
import '../../domain/models/task.dart';
import '../../domain/repositories/data_repository.dart';
import '../../data/repositories/sqlite_repository.dart';
import '../../data/repositories/hive_repository.dart';
import '../../core/logger.dart';

class TaskProvider with ChangeNotifier {
  final DataRepository _sqliteRepo = SqliteRepository();
  final DataRepository _hiveRepo = HiveRepository();

  late DataRepository _currentRepo;
  bool _isSqlite = true;
  List<Task> _tasks = [];

  TaskProvider() {
    _currentRepo = _sqliteRepo;
  }

  bool get isSqlite => _isSqlite;
  List<Task> get tasks => _tasks;
  String get currentSourceName => _currentRepo.sourceName;

  void toggleDatabase() {
    _isSqlite = !_isSqlite;
    _currentRepo = _isSqlite ? _sqliteRepo : _hiveRepo;
    Logger.info('Database switched to: ${_currentRepo.sourceName}');
    loadTasks();
  }

  Future<void> loadTasks() async {
    try {
      _tasks = await _currentRepo.getAllTasks();
      notifyListeners();
    } catch (e) {
      Logger.error('Error loading tasks', e);
    }
  }

  Future<void> addTask(String title, String description) async {
    final newTask = Task(title: title, description: description);
    await _currentRepo.addTask(newTask);
    await loadTasks();
  }

  Future<void> updateTask(Task task) async {
    await _currentRepo.updateTask(task);
    await loadTasks();
  }

  Future<void> deleteTask(int id) async {
    await _currentRepo.deleteTask(id);
    await loadTasks();
  }
}
