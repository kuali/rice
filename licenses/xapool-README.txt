The xapool.jar which is in this directory is a patched version.  It was built from the 1.5.0 src code and the following line was added to the org.enhydra.jdbc.pool.GenericPool at line 192:

if (life == null) break;

This prevents sporadic and random NPEs from being shot out of the bowels of XAPool when it's under significant load.  The NPEs we were getting were at line 200 in GenericPool:

if ((now - life.longValue()) > lifeTime) {
