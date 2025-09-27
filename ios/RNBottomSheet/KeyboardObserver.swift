import UIKit

final class KeyboardObserver {
    private var observers: [NSObjectProtocol] = []
    private let callback: (Bool, CGRect) -> Void

    init(callback: @escaping (Bool, CGRect) -> Void) {
        self.callback = callback
        subscribe()
    }

    private func subscribe() {
        let center = NotificationCenter.default
        observers.append(center.addObserver(forName: UIResponder.keyboardWillShowNotification, object: nil, queue: .main) { [weak self] notification in
            guard
                let info = notification.userInfo,
                let frameValue = info[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue
            else { return }
            self?.callback(true, frameValue.cgRectValue)
        })
        observers.append(center.addObserver(forName: UIResponder.keyboardWillHideNotification, object: nil, queue: .main) { [weak self] notification in
            guard
                let info = notification.userInfo,
                let frameValue = info[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue
            else { return }
            self?.callback(false, frameValue.cgRectValue)
        })
    }

    func dispose() {
        let center = NotificationCenter.default
        observers.forEach { center.removeObserver($0) }
        observers.removeAll()
    }

    deinit { dispose() }
}
