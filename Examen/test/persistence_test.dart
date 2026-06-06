import 'package:flutter_test/flutter_test.dart';
import 'package:sqflite_common_ffi/sqflite_ffi.dart';
import 'package:examen_dual_persistence/domain/models/task.dart';
import 'package:examen_dual_persistence/data/repositories/sqlite_repository.dart';
import 'package:examen_dual_persistence/presentation/providers/task_provider.dart';

void main() {
  // Initialize sqflite for ffi
  sqfliteFfiInit();
  databaseFactory = databaseFactoryFfi;

  group('Persistence Tests', () {
    test('SQLite Repository should add and retrieve tasks', () async {
      final repo = SqliteRepository();
      final task = Task(title: 'Test SQLite', description: 'Desc SQLite');

      await repo.addTask(task);
      final tasks = await repo.getAllTasks();

      expect(tasks.any((t) => t.title == 'Test SQLite'), true);
    });

    test('TaskProvider should toggle between databases', () {
      final provider = TaskProvider();

      expect(provider.isSqlite, true);
      expect(provider.currentSourceName.contains('SQLite'), true);

      provider.toggleDatabase();

      expect(provider.isSqlite, false);
      expect(provider.currentSourceName.contains('Hive'), true);
    });
  });
}
