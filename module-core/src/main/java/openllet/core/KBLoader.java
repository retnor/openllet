// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package openllet.core;

import java.util.logging.Logger;
import openllet.core.utils.FileUtils;
import openllet.shared.tools.Log;
import openllet.shared.tools.Logging;

/**
 * <p>
 * A generic class that allows the creation of Pellet KnowledgeBase instances and load ontologies. The actual parsing and loading operations is implemented by
 * the concrete extensions of this class where each implementation uses a different parser and loader, e.g. Jena or OWLAPI.
 * </p>
 * <p>
 * Loading to a KnowledgeBase instance is typically a two-phase process. First the ontology is parsed into a representation supported by the loader, e.g. Jena
 * Model or OWLAPI Ontology. Then this object is processed by Pellet and loaded to the KnowledgeBase class.
 * </p>
 * <p>
 * This class provides fined-grained control over how parsing and loading is done along with a convenience function to do everything at once. Concrete
 * implementations of this class provides access to the ontology object used by the underlying loader.
 * </p>
 *
 * @author Evren Sirin
 */
public abstract class KBLoader implements Logging
{
	public static final Logger _logger = Log.getLogger(KBLoader.class);

	@Override
	public Logger getLogger()
	{
		return _logger;
	}

	protected volatile String _inputFormat;

	/**
	 * Resets the loader and clear any previously loaded ontologies.
	 */
	public abstract void clear();

	/**
	 * Convenience function to {@link #clear() clear}, {@link #parse(String...) parse}, {@link #load() load}, and {@link #getKB()}. Note: this method previously
	 * accepted a list of regular expressions instead of file URIs. This behavior has been changed as of 2.0.0-rc7.
	 *
	 * @param fileNameList list of file URIs
	 * @return KnowledgeBase instance with the given ontologies loaded
	 * @see FileUtils#getFileURIs(String...)
	 */
	public KnowledgeBase createKB(final String... fileNameList)
	{
		clear();
		parse(fileNameList);
		load();
		return getKB();
	}

	/**
	 * Returns the KB instance associated with the loader. The function {@link #load()} should be called beforehand.
	 *
	 * @return KnowledgeBase instance
	 */
	public abstract KnowledgeBase getKB();

	/**
	 * Load any previously parsed ontologies
	 */
	public abstract void load();

	/**
	 * Parse one or more files to the internal representation supported by this loader. Note: this method previously accepted a list of regular expressions
	 * instead of file URIs. This behavior has been changed as of 2.0.0-rc7.
	 *
	 * @param fileNames list of file URIs
	 * @see FileUtils#getFileURIs(String...)
	 */
	public void parse(final String... fileNames)
	{
		_logger.fine(() -> "Parsing (" + fileNames.length + ") files");
		for (final String fileName : fileNames)
		{
			final String fileURI = FileUtils.getFileURI(fileName);
			parseFile(fileURI);
		}
		_logger.info("Parsing done.");
	}

	/**
	 * Instruct loader to respect or ignore imports.
	 *
	 * @param valueAsBoolean
	 */
	public abstract void setIgnoreImports(boolean valueAsBoolean);

	/**
	 * Parse a single file to the internal representation supported by this loader.
	 *
	 * @param fileURI the URI of the file to be loaded
	 */
	protected abstract void parseFile(String fileURI);

	@Override
	public String toString()
	{
		return getClass().getSimpleName();
	}

}
