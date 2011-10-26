import java.sql.Connection
import java.text.MessageFormat
import java.sql.ResultSet

abstract class RecordSelectTransform extends DbCommand {
    String table
    String pk_col
    Closure transform

    def RecordSelectTransform(String table, String pk_col, Closure transform) {
        this.table = table
        this.pk_col = pk_col
        this.transform = transform
    }

    def abstract String generateSelectSql()

    def String generateUpdateSql(Map row, Map updated) {
        "update " + this.table + " set " + updated.collect { k, v -> k + "=" + v }.join(',') + " where " + this.pk_col + "=" + row[this.pk_col]
    }

    def void performSql(sql) {
        println sql
    }

    def static Map getRow(ResultSet rs) {
        def row = [:]
        for (i in 1..rs.metaData.columnCount) {
            row[rs.metaData.getColumnName(i)] = rs.getObject(i)
        }
        row
    }

    def perform(Connection c, List<String> args) {
        def sql = generateSelectSql()
        println "Executing: " + sql
        ResultSet rs = c.createStatement().executeQuery(sql)
        while (rs.next()) {
            def row = getRow(rs)
            Map transformed = this.transform.call(this, row)
            if (transformed != null && !transformed.empty ) {
                performSql(generateUpdateSql(row, transformed))
            }
        }
    }
}