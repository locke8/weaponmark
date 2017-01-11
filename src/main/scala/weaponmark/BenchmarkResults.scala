package weaponmark

final case class BenchmarkResults(
                usesPerTurn: Int,
                turns:       Int,
    private var hits:        Int = 0,
    private var misses:      Int = 0,
    private var zeroDamage:  Int = 0,
    private var botches:     Int = 0,
    private var damage:      Int = 0) {

  // validate constructor parameters
  require(usesPerTurn > 0)
  require(turns > 0)
  require(hits == 0)
  require(misses == 0)
  require(zeroDamage == 0)
  require(botches == 0)
  require(damage == 0)

  // mutators to increment counts
  def incZeroDamage(): Unit = zeroDamage += 1
  def incBotches(): Unit = botches += 1
  def incMisses(): Unit = misses += 1
  def incHitsBy(h : Int): Unit = {
    require(h >= 0) // accumulators don't go backwards
    hits += h
  }
  def incDamageBy(d : Int): Unit = {
    require(d >= 0) // accumulators don't go backwards
    damage += d
  }
  // accessors for counts
  def totalHits: Int       = hits
  def totalMisses: Int     = misses
  def totalZeroDamage: Int = zeroDamage
  def totalBotches: Int    = botches
  def totalDamage: Int     = damage

  private val attemptedUses = usesPerTurn * turns
  // statistic accessors
  def hitsPerTurn: Double    = hits.toDouble / turns
  def damPerTurn: Double     = damage.toDouble / turns
  def hitsPerUse: Double     = hits.toDouble / attemptedUses
  def damPerUse: Double      = damage.toDouble / attemptedUses
  def pctMisses: Double      = (misses.toDouble / attemptedUses) * 100
  def pctBotches: Double     = (botches.toDouble / attemptedUses) * 100
  def pctZeroDam: Double     = (zeroDamage.toDouble / attemptedUses) * 100
  def pctIneffective: Double = pctMisses + pctBotches + pctZeroDam
}