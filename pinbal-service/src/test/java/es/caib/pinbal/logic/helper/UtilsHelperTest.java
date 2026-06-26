package es.caib.pinbal.logic.helper;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UtilsHelperTest {

    @Test
    public void listSplit_nullRetornaListaDeCincBuits() {
        List<List<String>> result = UtilsHelper.listSplit(null);
        assertNotNull(result);
        assertEquals(5, result.size());
        result.forEach(sub -> assertNotNull(sub));
    }

    @Test
    public void listSplit_buitRetornaListaDeCincBuits() {
        List<List<String>> result = UtilsHelper.listSplit(new ArrayList<>());
        assertEquals(5, result.size());
        result.forEach(sub -> assertTrue(sub.isEmpty()));
    }

    @Test
    public void listSplit_elementsMenorDe999AgrupaEnUnSubllista() {
        List<Integer> input = Arrays.asList(1, 2, 3);
        List<List<Integer>> result = UtilsHelper.listSplit(input);
        assertEquals(5, result.size());
        assertEquals(3, result.get(0).size());
        assertTrue(result.get(1).isEmpty());
    }

    @Test
    public void listSplit_exactament999ElementsRetornaUnSubllista() {
        List<Integer> input = new ArrayList<>();
        for (int i = 0; i < 999; i++) input.add(i);
        List<List<Integer>> result = UtilsHelper.listSplit(input);
        assertEquals(5, result.size());
        assertEquals(999, result.get(0).size());
    }

    @Test
    public void listSplit_mes1000ElementsParteixEnSubllistes() {
        List<Integer> input = new ArrayList<>();
        for (int i = 0; i < 2000; i++) input.add(i);
        List<List<Integer>> result = UtilsHelper.listSplit(input);
        assertTrue(result.size() >= 2);
        assertEquals(999, result.get(0).size());
        assertEquals(999, result.get(1).size());
        assertEquals(2, result.get(2).size());
    }

    @Test
    public void getListPartition_nullRetornaNull() {
        assertNull(UtilsHelper.getListPartition(null, 0));
    }

    @Test
    public void getListPartition_indexNegatiu_llancaException() {
        List<List<String>> splits = UtilsHelper.listSplit(null);
        assertThrows(IndexOutOfBoundsException.class, () -> UtilsHelper.getListPartition(splits, -1));
    }

    @Test
    public void getListPartition_indexMajorMida_llancaException() {
        List<List<String>> splits = UtilsHelper.listSplit(null);
        assertThrows(IndexOutOfBoundsException.class, () -> UtilsHelper.getListPartition(splits, 10));
    }

    @Test
    public void getListPartition_sublllistaBuida_retornaNull() {
        List<List<String>> splits = UtilsHelper.listSplit(null);
        assertNull(UtilsHelper.getListPartition(splits, 0));
    }

    @Test
    public void getListPartition_ambDades_retornaSubllista() {
        List<String> input = Arrays.asList("a", "b");
        List<List<String>> splits = UtilsHelper.listSplit(input);
        List<String> result = UtilsHelper.getListPartition(splits, 0);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void getNullIfEmpty_nullRetornaNull() {
        assertNull(UtilsHelper.getNullIfEmpty(null));
    }

    @Test
    public void getNullIfEmpty_buitRetornaNull() {
        assertNull(UtilsHelper.getNullIfEmpty(new ArrayList<>()));
    }

    @Test
    public void getNullIfEmpty_ambElementsRetornaMateixaLlista() {
        List<String> input = Arrays.asList("x");
        assertSame(input, UtilsHelper.getNullIfEmpty(input));
    }
}
