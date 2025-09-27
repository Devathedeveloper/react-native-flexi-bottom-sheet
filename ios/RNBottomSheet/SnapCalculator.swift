import UIKit

/// Resolves snap points expressed in pixels or percentages relative to the window height.
struct SnapCalculator {
    private(set) var snapPoints: [CGFloat] = []

    mutating func setSnapPoints(_ values: [Any], containerHeight: CGFloat) {
        precondition(!values.isEmpty, "snapPoints cannot be empty")
        var resolved: [CGFloat] = []
        for value in values {
            if let number = value as? NSNumber {
                resolved.append(CGFloat(number.doubleValue))
            } else if let string = value as? String {
                resolved.append(resolvePercentage(string, containerHeight: containerHeight))
            }
        }
        let unique = Array(Set(resolved)).sorted()
        snapPoints = unique
    }

    func closestSnap(to target: CGFloat, velocity: CGFloat) -> CGFloat {
        guard let first = snapPoints.first else { return target }
        if snapPoints.count == 1 { return first }
        var best = first
        var bestDistance = CGFloat.greatestFiniteMagnitude
        for point in snapPoints {
            let distance = abs(point - target)
            if distance < bestDistance {
                bestDistance = distance
                best = point
            }
        }
        if velocity > 400 { return snapPoints.first(where: { $0 >= target }) ?? snapPoints.last! }
        if velocity < -400 { return snapPoints.last(where: { $0 <= target }) ?? snapPoints.first! }
        return best
    }

    func index(for position: CGFloat) -> Int {
        guard !snapPoints.isEmpty else { return -1 }
        var bestIndex = 0
        var bestDistance = CGFloat.greatestFiniteMagnitude
        for (index, point) in snapPoints.enumerated() {
            let distance = abs(point - position)
            if distance < bestDistance {
                bestIndex = index
                bestDistance = distance
            }
        }
        return bestIndex
    }

    func clamped(_ position: CGFloat) -> CGFloat {
        guard let minValue = snapPoints.first, let maxValue = snapPoints.last else { return position }
        return min(max(position, minValue), maxValue)
    }

    private func resolvePercentage(_ value: String, containerHeight: CGFloat) -> CGFloat {
        let trimmed = value.trimmingCharacters(in: .whitespaces)
        guard trimmed.hasSuffix("%"), let numeric = Double(trimmed.dropLast()) else {
            return CGFloat((trimmed as NSString).doubleValue)
        }
        return containerHeight * CGFloat(numeric / 100.0)
    }
}
