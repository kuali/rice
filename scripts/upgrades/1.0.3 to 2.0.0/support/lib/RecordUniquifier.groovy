import java.sql.Connection
import java.text.MessageFormat
import java.sql.ResultSet

/**
 * A DbCommand that selects duplicate rows based on specified columns, and emits
 * sql to "uniquify" the row
 */
abstract class RecordUniquifier extends RecordSelectTransform {
    def static SELECT_DUPLICATES = """
        select * from {1} where ({0}) in (
            select {0} from {1} group by {0} having count(*) > 1
        )
        """

    def columns
    def transform

    def RecordUniquifier(String table, String pk_col, Closure transform, List<String> columns) {
        super(table, pk_col, transform)
        this.columns = columns
    }

    def String generateSelectSql() {
        new MessageFormat(SELECT_DUPLICATES).format([ this.columns.join(","), this.table ] as Object[])
    }

    def help() {
        return "Uniquifies " + this.table + " records that are duplicated on the following fields: " + this.columns
    }
}