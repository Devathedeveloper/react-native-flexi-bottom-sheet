import XCTest
@testable import RNBottomSheet

final class SnapCalculatorTests: XCTestCase {
    func testPercentageResolution() {
        var calculator = SnapCalculator()
        calculator.setSnapPoints(["50%", 300], containerHeight: 800)
        XCTAssertEqual(calculator.snapPoints.first, 300)
        XCTAssertTrue(calculator.snapPoints.contains(400))
    }

    func testClosestSnap() {
        var calculator = SnapCalculator()
        calculator.setSnapPoints([100, 300, 500], containerHeight: 800)
        XCTAssertEqual(calculator.closestSnap(to: 320, velocity: 0), 300)
        XCTAssertEqual(calculator.closestSnap(to: 320, velocity: 600), 500)
    }
}
