import 'package:hive_flutter/hive_flutter.dart';
import '../../domain/models/task.dart';
import '../../domain/repositories/data_repository.dart';
import '../../core/logger.dart';

class HiveRepository implements DataRepository {
  static const String boxName = 'tasksBox';

  Future<Box> get box async {
    if (!Hive.isBoxOpen(boxName)) {
      return await Hive.openBox(boxName);
    }
    return Hive.box(boxName);
  }

  @override
  String get sourceName => "Hive (NoSQL)";

  @override
  Future<List<Task>> getAllTasks() async {
    final b = await box;
    final List<Task> tasks = [];
    for (var i = 0; i < b.length; i++) {
      final Map<String, dynamic> map = Map<String, dynamic>.from(b.getAt(i));
      // Hive doesn't handle auto-increment IDs the same way, we use the index as ID if not present
      tasks.add(Task.fromMap({...map, 'id': i}));
    }
    Logger.debug('Reading from Hive: ${tasks.length} tasks found');
    return tasks;
  }

  @override
  Future<void> addTask(Task task) async {
    final b = await box;
    await b.add(task.toMap());
    Logger.info('Hive: Task inserted - ${task.title}');
  }

  @override
  Future<void> updateTask(Task task) async {
    final b = await box;
    if (task.id != null) {
      await b.putAt(task.id!, task.toMap());
      Logger.info('Hive: Task updated at index ${task.id} - ${task.title}');
    }
  }

  @override
  Future<void> deleteTask(int id) async {
    final b = await box;
    await b.deleteAt(id);
    Logger.info('Hive: Task deleted at index $id');
  }
}
