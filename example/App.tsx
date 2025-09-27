import React, { useMemo, useRef, useState } from 'react';
import { SafeAreaView, View, Text, Button, TextInput, FlatList, StyleSheet } from 'react-native';
import BottomSheet, {
  type BottomSheetProps,
  type BottomSheetRef,
  type KeyboardBehavior,
} from 'react-native-flexi-bottom-sheet';

type KeyboardMode = KeyboardBehavior;

const DATA = Array.from({ length: 50 }, (_, i) => `Item ${i + 1}`);

const App = () => {
  const sheetRef = useRef<BottomSheetRef>(null);
  const [behavior, setBehavior] = useState<KeyboardMode>('interactive');
  const [index, setIndex] = useState(-1);

  const snapPoints = useMemo<BottomSheetProps['snapPoints']>(
    () => ['25%', '50%', 600],
    [],
  );

  return (
    <SafeAreaView style={styles.safeArea}>
      <View style={styles.toolbar}>
        <Text style={styles.title}>Flexi Bottom Sheet</Text>
        <View style={styles.buttonRow}>
          <Button title="Collapse" onPress={() => sheetRef.current?.collapse()} />
          <Button title="Expand" onPress={() => sheetRef.current?.expand()} />
          <Button title="Close" onPress={() => sheetRef.current?.close()} />
        </View>
        <View style={styles.buttonRow}>
          {(['interactive', 'extend', 'fillParent'] as KeyboardMode[]).map(mode => (
            <Button key={mode} title={mode} onPress={() => setBehavior(mode)} />
          ))}
        </View>
      </View>
      <BottomSheet
        ref={sheetRef}
        index={index}
        snapPoints={snapPoints}
        keyboardBehavior={behavior}
        enablePanDownToClose
        onChange={setIndex}
        onOpen={() => console.log('opened')}
        onClose={() => console.log('closed')}
        backgroundColor="#ffffff"
        style={styles.sheet}
      >
        <View style={styles.content}>
          <Text style={styles.subtitle}>Keyboard demo ({behavior})</Text>
          <TextInput placeholder="Tap to show keyboard" style={styles.input} />
          <FlatList
            data={DATA}
            keyExtractor={item => item}
            style={styles.list}
            renderItem={({ item }) => <Text style={styles.item}>{item}</Text>}
          />
        </View>
      </BottomSheet>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  safeArea: { flex: 1, backgroundColor: '#f5f5f5' },
  toolbar: { padding: 16, backgroundColor: '#f5f5f5' },
  title: { fontSize: 20, fontWeight: '600', marginBottom: 8 },
  subtitle: { fontSize: 16, fontWeight: '500', marginBottom: 12 },
  buttonRow: { flexDirection: 'row', marginBottom: 12, justifyContent: 'space-between' },
  sheet: { flex: 1 },
  content: { padding: 16, height: '100%' },
  input: { borderWidth: 1, borderColor: '#ccc', padding: 12, borderRadius: 8, marginBottom: 16 },
  list: { flexGrow: 0, maxHeight: 300 },
  item: { paddingVertical: 8, borderBottomWidth: 1, borderBottomColor: '#eee' },
});

export default App;
