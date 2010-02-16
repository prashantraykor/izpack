package com.izforge.izpack.merge;

import com.izforge.izpack.matcher.MergeMatcher;
import com.izforge.izpack.merge.resolve.PathResolver;
import org.hamcrest.core.Is;
import org.hamcrest.text.StringContains;
import org.junit.Test;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test for merge jar
 *
 * @author Anthonin Bonnefoy
 */
public class JarMergeTest {

    @Test
    public void testAddJarContent() throws Exception {
        JarMerge jarMerge = new JarMerge(getClass().getResource("test/jar-hellopanel-1.0-SNAPSHOT.jar"));
        assertThat(jarMerge, MergeMatcher.isMergeableContainingFiles("jar/izforge/izpack/panels/hello/HelloPanel.class")
        );
    }

    @Test
    public void testMergeClassFromJarFile() throws Exception {
        List<Mergeable> jarMergeList = PathResolver.getMergeableFromPath("junit/framework/Assert.class");
        assertThat(jarMergeList.size(), Is.is(1));

        Mergeable jarMerge = jarMergeList.get(0);
        assertThat(jarMerge, MergeMatcher.isMergeableContainingFiles("junit/framework/Assert.class")
        );
    }


    @Test
    public void testFindPanelInJar() throws Exception {
        JarMerge jarMerge = new JarMerge(getClass().getResource("test/izpack-panel-5.0.0-SNAPSHOT.jar"));
        File file = jarMerge.find(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory() ||
                        pathname.getName().replaceAll(".class", "").equalsIgnoreCase("CheckedHelloPanel");
            }
        });
        assertThat(file.getAbsolutePath(), StringContains.containsString("com/izforge/izpack/panels/checkedhello/CheckedHelloPanel.class"));
    }


    @Test
    public void findFileInJarFoundWithURL() throws Exception {
        URL urlJar = ClassLoader.getSystemResource("com/izforge/izpack/merge/test/jar-hellopanel-1.0-SNAPSHOT.jar");
        URLClassLoader loader = URLClassLoader.newInstance(new URL[]{urlJar}, ClassLoader.getSystemClassLoader());

        JarMerge jarMerge = new JarMerge(loader.getResource("jar/izforge"));
        File file = jarMerge.find(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.getName().matches(".*HelloPanel\\.class") || pathname.isDirectory();
            }
        });
        assertThat(file.getName(), Is.is("HelloPanel.class"));
    }


    @Test
    public void mergeJarFoundWithURL() throws Exception {
        URL urlJar = ClassLoader.getSystemResource("com/izforge/izpack/merge/test/jar-hellopanel-1.0-SNAPSHOT.jar");
        URLClassLoader loader = URLClassLoader.newInstance(new URL[]{urlJar}, ClassLoader.getSystemClassLoader());

        JarMerge jarMerge = new JarMerge(loader.getResource("jar/izforge"), "com/dest");

        assertThat(jarMerge, MergeMatcher.isMergeableContainingFiles("com/dest/izpack/panels/hello/HelloPanel.class"));
    }
}
